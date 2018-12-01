package com.coco.winter.common.entity;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName ResponseBean
 * @Description 统一返回数据格式
 * @Author like
 * @Data 2018/11/10 16:22
 * @Version 1.0
 **/

@Data
public class ResponseBean {

    private Integer code;

    private String msg;

    private Object data;

    //用于存放token的值，jwtToken
    private Map<String, Object> token;
}
