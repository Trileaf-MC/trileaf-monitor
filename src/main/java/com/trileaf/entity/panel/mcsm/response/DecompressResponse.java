package com.trileaf.entity.panel.mcsm.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 解压文件响应
 *
 * @author 徐亚松
 * 2025/4/3 16:07
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DecompressResponse extends MCSManagerBaseResponse {
    private Boolean data;
}
