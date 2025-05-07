package cn.sanyeyun.entity.response;

import lombok.Data;

/**
 * 若依响应实体类
 *
 * @author 徐亚松
 * 2025-05-07 20:56
 **/
@Data
public class RuoYiResponse {
    private Integer code;
    private String msg;
    private Object data;

    public boolean isSuccess() {
        return code != null && code == 200;
    }
}
