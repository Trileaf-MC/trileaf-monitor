package com.trileaf.entity.panel.mcsm.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件内容响应
 *
 * @author 徐亚松
 * 2025/3/31 15:45
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileContentResponse extends MCSManagerBaseResponse {
    private String data;
}
