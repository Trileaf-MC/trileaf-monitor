package com.trileaf.entity.panel.mcsm.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 文件列表响应
 *
 * @author 徐亚松
 * 2025/3/31 14:53
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileListResponse extends MCSManagerBaseResponse {
    /**
     * 返回数据
     */
    private Data data;

    @lombok.Data
    public static class Data {
        /**
         * 文件项列表
         */
        private List<Item> items;

        /**
         * 当前页码
         */
        private int page;

        /**
         * 每页大小
         */
        private int pageSize;

        /**
         * 总项数
         */
        private int total;

        /**
         * 绝对路径
         */
        private String absolutePath;
    }

    @lombok.Data
    public static class Item {
        /**
         * 文件或文件夹名称
         */
        private String name;

        /**
         * 文件大小（字节）
         */
        private long size;

        /**
         * 最后修改时间
         */
        private String time;

        /**
         * 文件权限（Linux文件权限表示）
         */
        private int mode;

        /**
         * 类型（0 = 文件夹，1 = 文件）
         */
        private int type;
    }
}
