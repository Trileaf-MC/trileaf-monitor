package cn.sanyeyun.entity.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * CurseForge 响应
 *
 * @author 徐亚松
 * 2025-05-07 19:00
 **/
@Data
public class CurseForgeResponse {
    private CurseForgeData data;

    @Data
    public static class CurseForgeData {
        private boolean isCacheBuilt;
        private List<CurseForgeExactMatch> exactMatches;
        private List<Long> exactFingerprints;
        private List<Object> partialMatches;
        private Map<String, Object> partialMatchFingerprints;
        private List<Long> installedFingerprints;
        private List<Long> unmatchedFingerprints;
    }

    @Data
    public static class CurseForgeExactMatch {
        /**
         * 分类标签, 逗号分割,如冒险,科技
         */
        private String categories;
        private long id;
        private CurseForgeFile file;
        private List<CurseForgeFile> latestFiles;
    }

    @Data
    public static class CurseForgeFile {
        private long id;
        private long gameId;
        private long modId;
        private boolean isAvailable;
        private String displayName;
        private String fileName;
        private int releaseType;
        private int fileStatus;
        private List<CurseForgeHash> hashes;
        private String fileDate;
        private long fileLength;
        private long downloadCount;
        private String downloadUrl;
        private List<String> gameVersions;
        private List<CurseForgeSortableGameVersion> sortableGameVersions;
        private List<Object> dependencies;
        private long alternateFileId;
        private boolean isServerPack;
        private long fileFingerprint;
        private List<CurseForgeModule> modules;
    }

    @Data
    public static class CurseForgeHash {
        private String value;
        private int algo;
    }

    @Data
    public static class CurseForgeSortableGameVersion {
        private String gameVersionName;
        private String gameVersionPadded;
        private String gameVersion;
        private String gameVersionReleaseDate;
        private long gameVersionTypeId;
    }

    @Data
    public static class CurseForgeModule {
        private String name;
        private long fingerprint;
    }
}
