package cn.sanyeyun.trileafmonitormod.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class ExternalApiService {

    private final RestTemplate restTemplate;

    public ExternalApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 发送信息到外部接口
    public String sendMessageToExternalApi(String url, String message) {
        // 设置请求头，如果需要的话
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // 创建请求实体，包含信息和请求头
        HttpEntity<String> entity = new HttpEntity<>(message, headers);

        // 发送 POST 请求到外部接口并获取响应
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // 返回响应的主体
        return response.getBody();
    }
}
