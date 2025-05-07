package cn.sanyeyun.entity.response;

import lombok.Data;

import java.util.List;

/**
 * Modrinth 项目请求响应
 *
 * @author 徐亚松
 * 2025-05-06 15:37
 **/
@Data
public class ModrinthProjectResponse {
    // 项目Id
    private String id;
    private String client_side;
    private String server_side;
    private List<String> categories;
}
