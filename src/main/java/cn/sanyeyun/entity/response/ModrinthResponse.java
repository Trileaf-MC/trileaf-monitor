package cn.sanyeyun.entity.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * Modrinth Mod信息响应
 *
 * @author 徐亚松
 * 2025-05-07 18:20
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ModrinthResponse extends java.util.HashMap<String, ModrinthResponse.ModrinthModInfo> {

    @Data
    public static class ModrinthModInfo {
        /**
         * 客户端安装支持情况
         */
        private String client_side;
        /**
         * 服务端安装支持情况
         */
        private String server_side;
        /**
         * 分类标签, 逗号分割,如冒险,科技
         */
        private String categories;
        // 以上三个字段并非查询Mod的接口返回的,是查询项目的接口返回的
        private List<String> game_versions;
        private List<String> loaders;
        private String id;
        private String project_id;
        private String author_id;
        private boolean featured;
        private String name;
        private String version_number;
        private String changelog;
        private String changelog_url;
        private String date_published;
        private Long downloads;
        private String version_type;
        private String status;
        private String requested_status;
        private List<ModrinthFileInfo> files;
        private List<Object> dependencies;

    }

    @Data
    public static class ModrinthFileInfo {
        private Map<String, String> hashes;
        private String url;
        private String filename;
        private boolean primary;
        private long size;
        private String file_type;
    }


}
