package com.trileaf.entity.panel.mcsm.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实例详情响应
 *
 * @author 徐亚松
 * 2025/3/31 14:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InstanceDetailResponse extends MCSManagerBaseResponse {

    private RemoteServiceInstancesResponse.InstanceDetail data;

}
