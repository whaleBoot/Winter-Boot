package com.coco.winter.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @ClassName HttpRequestUtil
 * @Description 请求HttpRequest
 * @Author like
 * @Data 2018/11/28 9:31
 * @Version 1.0
 **/
@Component
public class HttpRequestUtil {


    private static RestTemplate restTemplate;


    public static String post2Hcm(String servletUrl, Map<String, Object> map) throws Exception {
        String json = JacksonJsonUtil.mapToJson(map);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(json, headers);
        ResponseEntity<String> resp = restTemplate.exchange(servletUrl, HttpMethod.POST, entity, String.class);
        return resp.getBody();
    }

    /**
     * @param restTemplate
     */
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        HttpRequestUtil.restTemplate = restTemplate;
    }
}
