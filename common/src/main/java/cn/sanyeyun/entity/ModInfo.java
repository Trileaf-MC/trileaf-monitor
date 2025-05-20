package cn.sanyeyun.entity;

import lombok.Data;

import java.util.List;

/**
 * @author 徐亚松
 * 2025-05-07 23:16
 **/
@Data
public class ModInfo {
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 数据来源
     */
    private String source;
    /**
     * Mod哈希,可能是sha1或sha512,用来确认mod唯一值
     */
    private String fileHash;
    /**
     * 分类标签, 逗号分割,如冒险,科技
     */
    private String categories;
    /**
     * 游戏版本,逗号分割
     */
    private String gameVersions;
    /**
     * 加载器类型,逗号分割
     */
    private String loaders;
    /**
     * 客户端安装支持情况
     */
    private String clientSide;
    /**
     * 服务端安装支持情况
     */
    private String serverSide;
    /**
     * ModId
     */
    private String modId;
    /**
     * 项目Id
     */
    private String projectId;
    /**
     * 作者Id
     */
    private String authorId;
    /**
     * 名称
     */
    private String name;
    /**
     * Mod版本
     */
    private String versionNumber;
    /**
     * Mod发布时间
     */
    private String datePublished;
    /**
     * 下载次数
     */
    private Long downloads;
    /**
     * 文件信息,包含下载地址
     */
    private List<FileInfo> files;


    /**
     * 文件信息对象
     *
     * @author 徐亚松
     * 2025/4/25 16:36
     */
    @Data
    public static class FileInfo {
        private Hashes hashes;
        private String url;
        private String filename;
        private boolean primary;
        private long size;
        private String file_type;

        @Data
        public static class Hashes {
            private String sha512;
            private String md5;
            private String sha1;
        }
    }
}
