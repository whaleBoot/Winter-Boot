package com.coco.winter.exception;

import lombok.Data;

/**
 * @ClassName CustomException
 * @Description 调用HCM流程接口时，errorCode不为0时进行异常捕获 自定义异常
 * @Author like
 * @Data 2018/11/23 19:40
 * @Version 1.0
 **/

@Data
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = -6114700933498491185L;

    private Integer errorCode;
    private String errorMessage;

    public CustomException() {
    }

    public CustomException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public CustomException(Integer errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
