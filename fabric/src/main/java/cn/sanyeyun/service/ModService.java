package cn.sanyeyun.service;

import cn.sanyeyun.cache.GlobalCache;
import cn.sanyeyun.constant.CommonConstants;
import cn.sanyeyun.entity.ModInfo;
import cn.sanyeyun.entity.response.CurseForgeResponse;
import cn.sanyeyun.entity.response.ModrinthProjectResponse;
import cn.sanyeyun.entity.response.ModrinthResponse;
import cn.sanyeyun.enums.PlatformType;
import cn.sanyeyun.enums.SourceType;
import cn.sanyeyun.utils.DateUtil;
import cn.sanyeyun.utils.HashUtil;
import cn.sanyeyun.utils.HttpRequestUtil;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Mod操作
 *
 * @author 徐亚松
 * 2025-05-07 14:26
 **/
public class ModService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModService.class);
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    public static void collectModInfoAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                // 1. 收集 Mod 文件
                List<File> jarFiles = findModJarFiles();
                if (jarFiles.isEmpty()) {
                    GlobalCache.getModInfos().complete(Collections.emptyList());
                    GlobalCache.getCompletelyUnmatchedFiles().complete(Collections.emptyList());
                    return;
                }

                // 2. 计算 SHA1
                Map<String, File> sha1Map = calculateSha1Hashes(jarFiles);

                // 3. 调用 Modrinth 查询
                ModrinthResponse modrinthResponse = queryModrinth(sha1Map.keySet());

                // 4. 收集未匹配的 SHA1 并计算 MurmurHash
                Map<Long, File> murmurMap = handleUnmatchedSha1(sha1Map, modrinthResponse);

                // 5. 调用 CurseForge 查询
                CurseForgeResponse curseForgeResponse = queryCurseForge(murmurMap.keySet());

                // 6. 收集没有任何匹配的文件
                List<File> completelyUnmatchedFiles = collectUnmatchedFiles(murmurMap, curseForgeResponse);
                GlobalCache.getCompletelyUnmatchedFiles().complete(completelyUnmatchedFiles);

                // 7. 组装 ModInfo
                List<ModInfo> modInfos = buildFrom(modrinthResponse, curseForgeResponse);
                GlobalCache.getModInfos().complete(modInfos);



            } catch (Exception e) {
                // 异步异常也要 completeExceptionally
                GlobalCache.getModInfos().completeExceptionally(e);
                GlobalCache.getCompletelyUnmatchedFiles().completeExceptionally(e);
                LOGGER.error(e.toString());
            }
        });
    }


    /**
     * 收集Mod信息
     *
     * @return boolean 是否采集成功
     * @author 徐亚松
     * <p>2025/5/7 22:24</p>
     */
/*    public static boolean collectModInfo() {
        // 收集Mod
        List<File> jarFiles = findModJarFiles();
        if (jarFiles.isEmpty()) return false;
        // 封装请求参数
        Map<String, File> sha1Map = calculateSha1Hashes(jarFiles);
        // 调用Modrinth查询
        ModrinthResponse modrinthResponse = queryModrinth(sha1Map.keySet());

        // 封装请求参数
        Map<Long, File> murmurMap = handleUnmatchedSha1(sha1Map, modrinthResponse);
        // 调用CurseForge查询
        CurseForgeResponse curseForgeResponse = queryCurseForge(murmurMap.keySet());

        // 收集没有任何匹配的mod
        List<File> completelyUnmatchedFiles = collectUnmatchedFiles(murmurMap, curseForgeResponse);
        GlobalCache.setCompletelyUnmatchedFiles(completelyUnmatchedFiles);

        // 组装成请求数据
        List<ModInfo> modInfos = buildFrom(modrinthResponse, curseForgeResponse);
        GlobalCache.setModInfos(modInfos);
        return !modInfos.isEmpty();
        // String s = HttpRequestUtil.postMultipart(CommonConstants.MOD_REGISTER, GSON.toJson(modInfos), completelyUnmatchedFiles);

    }*/

    /**
     * 构建请求数据
     *
     * @param modrinthResponse   modrinth响应
     * @param curseForgeResponse curseForge响应
     * @return {@link List<ModInfo>}
     * @author 徐亚松 2025/5/7 20:31
     */
    private static List<ModInfo> buildFrom(ModrinthResponse modrinthResponse,
                                           CurseForgeResponse curseForgeResponse) {
        List<ModInfo> modInfos = new ArrayList<>();

        modrinthResponse.forEach((key, value) -> {
            ModInfo modInfo = new ModInfo();
            modInfo.setName(value.getName());
            modInfo.setSource(SourceType.MODRINTH.getValue());
            modInfo.setFileHash(key);
            modInfo.setGameVersions(String.join(",", value.getGame_versions()));
            modInfo.setLoaders(String.join(",", value.getLoaders()));
            modInfo.setModId(value.getId());
            modInfo.setProjectId(value.getProject_id());
            modInfo.setAuthorId(value.getAuthor_id());
            modInfo.setVersionNumber(value.getVersion_number());
            modInfo.setCategories(value.getCategories());
            modInfo.setClientSide(value.getClient_side());
            modInfo.setServerSide(value.getServer_side());

            Date date = DateUtil.parseDateToDate(value.getDate_published());
            modInfo.setDatePublished(DateUtil.formatDateToStr(date));
            modInfo.setDownloads(value.getDownloads());

            List<ModInfo.FileInfo> fileInfos = new ArrayList<>();
            value.getFiles().forEach(v -> {
                ModInfo.FileInfo fileInfo = new ModInfo.FileInfo();
                ModInfo.FileInfo.Hashes hashes1 = new ModInfo.FileInfo.Hashes();
                hashes1.setSha1(v.getHashes().getOrDefault("sha1", ""));
                hashes1.setSha512(v.getHashes().getOrDefault("sha512", ""));
                fileInfo.setHashes(hashes1);
                fileInfo.setUrl(v.getUrl());
                fileInfo.setFilename(v.getFilename());
                modInfo.setFileName(v.getFilename());
                fileInfo.setSize(v.getSize());
                fileInfo.setFile_type(v.getFile_type());
                fileInfos.add(fileInfo);
            });
            modInfo.setFiles(fileInfos);
            modInfos.add(modInfo);
        });

        curseForgeResponse.getData().getExactMatches().forEach(v -> {
            ModInfo modInfo = new ModInfo();
            CurseForgeResponse.CurseForgeFile file = v.getFile();
            modInfo.setFileName(file.getDisplayName());
            modInfo.setSource(SourceType.CURSEFORGE.getValue());
            modInfo.setFileHash(String.valueOf(file.getFileFingerprint()));
            modInfo.setGameVersions(String.join(",", file.getGameVersions()));
            String loaders = file.getSortableGameVersions().stream()
                    .filter(version -> "0".equals(version.getGameVersionPadded()))
                    .map(CurseForgeResponse.CurseForgeSortableGameVersion::getGameVersionName)
                    .collect(Collectors.joining(","));
            modInfo.setLoaders(loaders);
            modInfo.setModId(String.valueOf(file.getModId()));
            modInfo.setProjectId(String.valueOf(v.getId()));
            modInfo.setVersionNumber(null);

            Date date = DateUtil.parseDateToDate(file.getFileDate());
            String s = DateUtil.formatDateToStr(date);
            modInfo.setDatePublished(s);

            modInfo.setDownloads(file.getDownloadCount());

            List<ModInfo.FileInfo> fileInfos = new ArrayList<>();
            ModInfo.FileInfo fileInfo = new ModInfo.FileInfo();
            Map<Integer, String> hashMap = file.getHashes().stream()
                    .collect(Collectors.toMap(
                            CurseForgeResponse.CurseForgeHash::getAlgo,
                            CurseForgeResponse.CurseForgeHash::getValue,
                            (existing, replacement) -> replacement // 如果有重复的 algo，则用后面的覆盖
                    ));


            ModInfo.FileInfo.Hashes hashes1 = new ModInfo.FileInfo.Hashes();
            hashes1.setSha1(hashMap.getOrDefault(1, ""));
            hashes1.setMd5(hashMap.getOrDefault(2, ""));
            fileInfo.setHashes(hashes1);

            fileInfo.setUrl(file.getDownloadUrl());
            fileInfo.setFilename(file.getFileName());
            fileInfo.setSize(file.getFileLength());
            fileInfos.add(fileInfo);
            modInfo.setFiles(fileInfos);
            modInfos.add(modInfo);
        });

        return modInfos;

    }

    /**
     * 找到所有 .jar 文件
     *
     * @return {@link List<File>} Jar集合
     * @author 徐亚松
     * <p>2025/5/7 19:11</p>
     */
    private static List<File> findModJarFiles() {
        File modFolder = new File(System.getProperty("user.dir"), "mods");
        if (!modFolder.exists() || !modFolder.isDirectory()) {
            LOGGER.warn("mods 文件夹不存在！");
            return Collections.emptyList();
        }

        File[] jars = modFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            LOGGER.warn("未找到任何 mod jar 文件。");
            return Collections.emptyList();
        }

        return Arrays.asList(jars);
    }

    /**
     * 计算sha1
     *
     * @param jarFiles jar集合
     * @return {@link Map<String,File>}
     * @author 徐亚松
     * <p>2025/5/7 19:11</p>
     */
    private static Map<String, File> calculateSha1Hashes(List<File> jarFiles) {
        Map<String, File> map = new HashMap<>();
        for (File jar : jarFiles) {
            try {
                String sha1 = HashUtil.hashFile(jar.getAbsolutePath(), "SHA-1");
                map.put(sha1, jar);
                LOGGER.info("计算 SHA-1 成功: {} -> {}", jar.getName(), sha1);
            } catch (Exception e) {
                LOGGER.error("计算 SHA-1 失败: {}", jar.getName(), e);
            }
        }
        return map;
    }


    /**
     * 查询 Modrinth，通过 SHA-1 集合查找 Mod 信息
     *
     * @param sha1Set sha1集合
     * @return {@link ModrinthResponse} 返回 Modrinth 查询结果
     * @author 徐亚松
     * <p>2025/5/7 19:11</p>
     */
    private static ModrinthResponse queryModrinth(Set<String> sha1Set) {
        try {
            // 构建请求 JSON
            JsonObject json = new JsonObject();
            JsonArray hashes = new JsonArray();
            sha1Set.forEach(hashes::add);  // 将所有 SHA-1 添加到数组
            json.add("hashes", hashes);
            json.addProperty("algorithm", "sha1"); // 指定使用 SHA-1 算法

            // 发送 POST 请求到 Modrinth 查询接口
            String result = HttpRequestUtil.post(CommonConstants.VERSION_FILES, json.toString(), PlatformType.MODRINTH);
            if (result == null || result.isBlank()) {
                LOGGER.warn("Modrinth 返回为空，sha1Set: {}", sha1Set);
                return new ModrinthResponse(); // 返回空对象，避免 NPE
            }

            // 解析 JSON 为 ModrinthResponse
            ModrinthResponse modrinthResponse = GSON.fromJson(result, ModrinthResponse.class);
            if (modrinthResponse == null || modrinthResponse.isEmpty()) {
                LOGGER.warn("Modrinth 解析结果为空，返回空对象");
                return new ModrinthResponse();
            }

            // 收集所有 project_id，用于后续查询项目详细信息
            List<String> projectIds = modrinthResponse.values().stream()
                    .map(ModrinthResponse.ModrinthModInfo::getProject_id)
                    .filter(Objects::nonNull)
                    .toList();

            if (projectIds.isEmpty()) {
                // 如果没有任何 project_id，直接返回已有的响应
                return modrinthResponse;
            }

            // 通过 GET 请求获取项目详细信息
            Map<String, Object> params = Map.of("ids", projectIds);
            String resultProject = HttpRequestUtil.get(CommonConstants.PROJECTS, params, PlatformType.MODRINTH);

            if (resultProject == null || resultProject.isBlank()) {
                LOGGER.warn("Modrinth 项目详情返回为空");
                return modrinthResponse; // 返回基础响应
            }

            // 解析项目详情列表
            Type listType = new TypeToken<List<ModrinthProjectResponse>>() {}.getType();
            List<ModrinthProjectResponse> modrinthProjectResponse = GSON.fromJson(resultProject, listType);
            if (modrinthProjectResponse == null || modrinthProjectResponse.isEmpty()) {
                LOGGER.warn("Modrinth 项目详情解析为空");
                return modrinthResponse;
            }

            // 将项目详情列表转换为 Map，便于快速查找
            Map<String, ModrinthProjectResponse> projectMap = modrinthProjectResponse.stream()
                    .filter(p -> p.getId() != null)
                    .collect(Collectors.toMap(ModrinthProjectResponse::getId, p -> p, (a, b) -> a));

            // 将查询到的项目详细信息填充到 ModrinthResponse 中
            modrinthResponse.forEach((sha1, modInfo) -> {
                if (modInfo == null || modInfo.getProject_id() == null) return;
                ModrinthProjectResponse project = projectMap.get(modInfo.getProject_id());
                if (project != null) {
                    modInfo.setClient_side(project.getClient_side());
                    modInfo.setServer_side(project.getServer_side());
                    modInfo.setCategories(project.getCategories() != null ? String.join(",", project.getCategories()) : null);
                }
            });

            return modrinthResponse;

        } catch (Exception e) {
            // 捕获所有异常，防止影响调用者
            LOGGER.error("查询 Modrinth 失败，sha1Set: {}", sha1Set, e);
            return new ModrinthResponse(); // 返回空对象，保证安全
        }
    }


    /**
     * 根据返回值收集未匹配的Mod 也就是Modrinth上没有查询到的Mod 并计算murmurHash2
     *
     * @param sha1Map  sha1集合
     * @param response Modrinth响应
     * @return {@link Map<Long,File>}
     * @author 徐亚松
     * <p>2025/5/7 19:12</p>
     */
    private static Map<Long, File> handleUnmatchedSha1(Map<String, File> sha1Map, ModrinthResponse response) {
        Map<Long, File> murmurMap = new HashMap<>();
        for (Map.Entry<String, File> entry : sha1Map.entrySet()) {
            if (!response.containsKey(entry.getKey())) {
                File file = entry.getValue();
                LOGGER.warn("未找到文件: {} (SHA-1: {})", file.getName(), entry.getKey());
                try {
                    long murmur = HashUtil.murmurHash2(file.getAbsolutePath());
                    murmurMap.put(murmur, file);
                    LOGGER.info("计算 MurmurHash2 成功: {} -> {}", file.getName(), murmur);
                } catch (IOException e) {
                    LOGGER.error("计算 MurmurHash2 失败: {}", file.getName(), e);
                }
            }
        }
        return murmurMap;
    }

    /**
     * 请求CurseForge
     *
     * @param fingerprints 指纹集合
     * @return {@link CurseForgeResponse}
     * @author 徐亚松
     * <p>2025/5/7 19:13</p>
     */

    private static CurseForgeResponse queryCurseForge(Set<Long> fingerprints) {
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        fingerprints.forEach(array::add);
        json.add("fingerprints", array);

        CurseForgeResponse curseForgeResponse = new CurseForgeResponse();
        try {
            // 调用接口
            String result = HttpRequestUtil.post(CommonConstants.FINGERPRINTS, json.toString(), PlatformType.CURSEFORGE);
            if (result == null || result.isBlank()) {
                LOGGER.warn("CurseForge 指纹查询返回为空");
                return curseForgeResponse; // 返回空对象
            }

            curseForgeResponse = GSON.fromJson(result, CurseForgeResponse.class);

            List<Long> modIds = curseForgeResponse.getData().getExactMatches().stream()
                    .map(v -> v.getFile().getId()).toList();
            if (modIds.isEmpty()) {
                return curseForgeResponse;
            }

            JsonObject json1 = new JsonObject();
            JsonArray array1 = new JsonArray();
            modIds.forEach(array1::add);
            json1.add("modIds", array1);

            // 请求 MOD 详情
            String post = HttpRequestUtil.post(CommonConstants.MODS, json1.toString(), PlatformType.CURSEFORGE);
            if (post == null || post.isBlank()) {
                LOGGER.warn("CurseForge MOD 查询返回为空");
                return curseForgeResponse;
            }

            // 安全解析 JSON
            JsonObject root;
            try {
                root = JsonParser.parseString(post).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                LOGGER.error("解析 CurseForge MOD JSON 出错", e);
                return curseForgeResponse;
            }

            JsonArray dataArray = root.getAsJsonArray("data");
            if (dataArray == null) {
                LOGGER.warn("CurseForge MOD JSON 中没有 data 数组");
                return curseForgeResponse;
            }

            Map<Long, String> modCategoriesMap = new HashMap<>();
            for (JsonElement element : dataArray) {
                if (element == null || element.isJsonNull()) continue;
                JsonObject mod = element.getAsJsonObject();
                long modId = mod.get("id").getAsLong();
                JsonArray categories = mod.getAsJsonArray("categories");
                List<String> slugList = new ArrayList<>();
                if (categories != null) {
                    for (JsonElement catElement : categories) {
                        if (catElement.isJsonObject()) {
                            JsonObject category = catElement.getAsJsonObject();
                            if (category.has("slug") && !category.get("slug").isJsonNull()) {
                                slugList.add(category.get("slug").getAsString());
                            }
                        }
                    }
                }
                modCategoriesMap.put(modId, String.join(",", slugList));
            }

            // 设置类别
            curseForgeResponse.getData().getExactMatches().forEach(v -> {
                if (v != null) v.setCategories(modCategoriesMap.get(v.getId()));
            });

        } catch (Exception e) {
            LOGGER.error("查询 CurseForge 失败", e);
        }

        return curseForgeResponse;
    }



    /*  private static CurseForgeResponse queryCurseForge(Set<Long> fingerprints) {
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        fingerprints.forEach(array::add);
        json.add("fingerprints", array);

        String result = HttpRequestUtil.post(CommonConstants.FINGERPRINTS, json.toString(), PlatformType.CURSEFORGE);
        CurseForgeResponse curseForgeResponse = GSON.fromJson(result, CurseForgeResponse.class);

        List<Long> modIds = curseForgeResponse.getData().getExactMatches().stream()
                .map(v -> v.getFile().getId()).toList();
        // 如果没有 modId，直接返回基础响应
        if (modIds.isEmpty()) {
            return curseForgeResponse;
        }
        JsonObject json1 = new JsonObject();
        JsonArray array1 = new JsonArray();
        modIds.forEach(array1::add);
        json1.add("modIds", array1);

        String post = HttpRequestUtil.post(CommonConstants.MODS, json1.toString(), PlatformType.CURSEFORGE);
        JsonObject root = JsonParser.parseString(post).getAsJsonObject();
        JsonArray dataArray = root.getAsJsonArray("data");

        // 用于存储每个 mod id 对应的 slug 字符串
        Map<Long, String> modCategoriesMap = new HashMap<>();

        for (JsonElement element : dataArray) {
            JsonObject mod = element.getAsJsonObject();
            long modId = mod.get("id").getAsInt();
            JsonArray categories = mod.getAsJsonArray("categories");

            List<String> slugList = new ArrayList<>();
            for (JsonElement catElement : categories) {
                JsonObject category = catElement.getAsJsonObject();
                if (category.has("slug") && !category.get("slug").isJsonNull()) {
                    slugList.add(category.get("slug").getAsString());
                }
            }

            String slugStr = String.join(",", slugList);
            modCategoriesMap.put(modId, slugStr);
        }

        curseForgeResponse.getData().getExactMatches().forEach(v -> {
            if (v == null) return;
            v.setCategories(modCategoriesMap.get(v.getId()));
        });

        return curseForgeResponse;
    }
*/
    /**
     * 收集 modrinth和forge都没有匹配的Mod
     *
     * @param murmurMap murmur指纹集合
     * @param response  forge响应
     * @return {@link List< File>}
     * @author 徐亚松
     * <p>2025/5/7 19:16</p>
     */
    private static List<File> collectUnmatchedFiles(Map<Long, File> murmurMap, CurseForgeResponse response) {
        List<File> unmatchedFiles = new ArrayList<>();
        Set<Long> matchedFingerprints = new HashSet<>(response.getData().getExactFingerprints());

        for (Map.Entry<Long, File> entry : murmurMap.entrySet()) {
            if (!matchedFingerprints.contains(entry.getKey())) {
                unmatchedFiles.add(entry.getValue());
            }
        }

        return unmatchedFiles;
    }


    /**
     * 加载Mod信息,并缓存起来
     *
     * @return
     * @author 徐亚松
     * <p>2025/5/7 14:39</p>
     */
    /*
    public static void collectModInfo1() {
        final File MODS_FOLDER = new File(System.getProperty("user.dir"), "mods");

        if (!MODS_FOLDER.exists() || !MODS_FOLDER.isDirectory()) {
            LOGGER.warn("mods 文件夹不存在！");
            return;
        }

        File[] jarFiles = MODS_FOLDER.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            LOGGER.warn("未找到任何 mod jar 文件。");
            return;
        }

        Map<String, File> sha1Map = new HashMap<>();
        for (File jar : jarFiles) {
            try {
                String sha1 = HashUtil.hashFile(jar.getAbsolutePath(), "SHA-1");
                sha1Map.put(sha1, jar);
                LOGGER.info("计算 SHA-1 成功: {} -> {}", jar.getName(), sha1);
            } catch (Exception e) {
                LOGGER.error("计算 SHA-1 失败: {}", jar.getName(), e);
            }
        }

        // 构建请求参数
        JsonObject jsonObject = new JsonObject();
        JsonArray hashes = new JsonArray();
        for (String sha1 : sha1Map.keySet()) {
            hashes.add(sha1);
        }
        jsonObject.add("hashes", hashes);
        jsonObject.addProperty("algorithm", "sha1");

        // 先去调用modrinth接口
        String modrinthJsonString = HttpRequestUtil.post(CommonConstants.VERSION_FILES, jsonObject.toString(), PlatformType.MODRINTH);
        ModrinthResponse modrinthResponse = GSON.fromJson(modrinthJsonString, ModrinthResponse.class);


        // 遍历所有计算的 SHA-1，查看哪些没有命中，然后去计算murmurHash2
        Map<Long, File> murmurHashMap = new HashMap<>();

        for (Map.Entry<String, File> entry : sha1Map.entrySet()) {
            String sha1 = entry.getKey();
            File jar = entry.getValue();

            if (!modrinthResponse.containsKey(sha1)) {
                LOGGER.warn("未找到文件: {} (SHA-1: {})", jar.getName(), sha1);
                try {
                    Long murmurHash = HashUtil.murmurHash2(jar.getAbsolutePath());
                    murmurHashMap.put(murmurHash, jar);
                    LOGGER.info("计算 MurmurHash2 成功: {} -> {}", jar.getName(), murmurHash);
                } catch (IOException e) {
                    LOGGER.error("计算 MurmurHash2 失败: {}", jar.getName(), e);
                }
            }
        }

        JsonObject curseforgeRequest = new JsonObject();
        JsonArray fingerprints = new JsonArray();

        for (Long hash : murmurHashMap.keySet()) {
            fingerprints.add(hash);
        }

        curseforgeRequest.add("fingerprints", fingerprints);

        String curseforgeJsonString = HttpRequestUtil.post(CommonConstants.FINGERPRINTS, curseforgeRequest.toString(), PlatformType.CURSEFORGE);
        CurseForgeResponse response = GSON.fromJson(curseforgeJsonString, CurseForgeResponse.class);

    }
    */
}

