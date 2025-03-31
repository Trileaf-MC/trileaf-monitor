package com.trileaf.entity.mcsm.request;

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
     * 守护进程ID。每个守护进程在系统中有唯一的ID。
     */
    private String remote_uuid;

    /**
     * 请求的页码。用于分页查询时指定当前页。
     */
    private int page;

    /**
     * 每页大小。表示每页期望返回的数据条数。
     */
    private int page_size;

    /**
     * 实例名称。标识具体的实例，可以为空，当不需要特定实例时可不填。
     */
    private String instance_name;

    /**
     * 状态。用于过滤查询结果的状态条件，必填项。
     */
    private String status;
}
