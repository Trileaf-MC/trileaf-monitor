package com.trileaf.entity.panel.mcsm.response;

import lombok.Data;

/**
 * MCSM响应基类
 *
 * @author 徐亚松
 * 2025/3/28 10:02
 * @see <a href="https://docs.mcsmanager.com/zh_cn/apis/get_apikey.html">MCSManager Api文档</a>
 */
@Data
public class MCSManagerBaseResponse {
    /**
     * 状态码
     * <p style="color:green">200:成功</p>
     * <p style="color:red">400:请求参数不正确</p>
     * <p style="color:red">403:权限不足</p>
     * <p style="color:red">500:程序错误</p>
     */
    private String status;
    /**
     * 请求完成处理的时间可用于测量延迟。
     */
    private Long time;
}
