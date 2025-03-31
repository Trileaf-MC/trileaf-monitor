package com.trileaf.entity.panel.mcsm.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCSM请求参数
 *
 * @author 徐亚松
 * 2025-03-28 21:48
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MCSManagerRequest {
    /**
     * 节点Id
     */
    private String remote_uuid;

    /**
     * 实例Id
     */
    private String uuid;

    /**
     * 请求的页码。用于分页查询时指定当前页。
     */
    private Integer page;

    /**
     * 每页大小。表示每页期望返回的数据条数。
     */
    private Integer page_size;

    /**
     * 实例名称。标识具体的实例，可以为空，当不需要特定实例时可不填。
     */
    private String instance_name;

    /**
     * 状态。用于过滤查询结果的状态条件
     */
    private String status;

    /**
     * 文件（名称或目录）路径
     */
    private String target;

    /**
     * 文件名应该是
     */
    private String file_name;
}
