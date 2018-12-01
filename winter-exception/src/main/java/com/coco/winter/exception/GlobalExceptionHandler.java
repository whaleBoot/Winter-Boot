package com.coco.winter.exception;

import com.coco.winter.common.entity.ResponseBean;
import com.coco.winter.common.serviceImpl.GetNewJwtToken;
import com.coco.winter.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@CrossOrigin
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @Autowired
    GetNewJwtToken getNewJwtToken;

    private static final String LOG_EXCEPTION_FORMAT = "Capture Exception By GlobalExceptionHandler: Code: %s Detail: %s";

    private static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * 运行时异常
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseBean runtimeExceptionHandler(RuntimeException ex, HttpServletRequest request) {
        log.info("code:1,运行时换异常");
        return exceptionFormat(1, ex, request);
    }

    /**
     * 空指针异常
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseBean nullPointerExceptionHandler(NullPointerException ex, HttpServletRequest request) {
        log.info("code:2,空指针异常");
        return exceptionFormat(2, ex, request);
    }


    /**
     * 类型转换异常
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(ClassCastException.class)
    public ResponseBean classCastExceptionHandler(ClassCastException ex, HttpServletRequest request) {
        log.info("code:3,类型转换异常");
        return exceptionFormat(3, ex, request);
    }


    /**
     * IO异常
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(IOException.class)
    public ResponseBean iOExceptionHandler(IOException ex, HttpServletRequest request) {
        log.info("code:4,IO异常");
        return exceptionFormat(4, ex, request);
    }


    /**
     * 未知方法异常
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(NoSuchMethodException.class)
    public ResponseBean noSuchMethodExceptionHandler(NoSuchMethodException ex, HttpServletRequest request) {
        log.info("code:5,未知方法异常");
        return exceptionFormat(5, ex, request);
    }


    /**
     * 数组越界异常
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseBean indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException ex, HttpServletRequest request) {
        log.info("code:6,数组越界异常");
        return exceptionFormat(6, ex, request);
    }


    /**
     * 400错误
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseBean requestNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.info("code:7,400错误----->requestNotReadable");
        return exceptionFormat(7, ex, request);
    }

    /**
     * 400错误
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({TypeMismatchException.class})
    public ResponseBean requestTypeMismatch(TypeMismatchException ex, HttpServletRequest request) {
        log.info("code:8,400错误----->TypeMismatchException");
        return exceptionFormat(8, ex, request);
    }

    /**
     * 400错误
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseBean requestMissingServletRequest(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.info("code:9,400错误----->MissingServletRequest");
        return exceptionFormat(9, ex, request);
    }

    /**
     * 405错误
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseBean request405(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.info("code:10,405错误");
        return exceptionFormat(10, ex, request);
    }

    /**
     * 406错误
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    public ResponseBean request406(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {
        log.info("code:11,406错误");
        return exceptionFormat(11, ex, request);
    }

    /**
     * 500错误
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
    public ResponseBean server500(RuntimeException ex, HttpServletRequest request) {
        log.info("code:12,500错误");
        return exceptionFormat(12, ex, request);
    }


    /**
     * 栈溢出
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({StackOverflowError.class})
    public ResponseBean requestStackOverflow(StackOverflowError ex, HttpServletRequest request) {
        log.info("code:13,栈溢出");
        return exceptionFormat(13, ex, request);
    }


    /**
     * 其他错误
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({Exception.class})
    public ResponseBean exception(Exception ex, HttpServletRequest request) {
        log.info("code:14,未知异常");
        return exceptionFormat(14, ex, request);
    }


    /**
     * 自定义异常捕获
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({MyException.class})
    public ResponseBean myException(MyException ex, HttpServletRequest request) {
        log.info("code:999,自定义异常捕获MyException");
        return exceptionFormat(999, ex, request);
    }

    /**
     * 自定义异常
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({CustomException.class})
    public ResponseBean customException(CustomException ex, HttpServletRequest request) {
        return customExceptionFormat(ex.getErrorCode(), ex.getErrorMessage(), request);
    }

    /**
     * 格式化
     *
     * @param code
     * @param ex
     * @param request
     * @param <T>
     * @return
     */
    private <T extends Throwable> ResponseBean exceptionFormat(Integer code, T ex, HttpServletRequest request) {
        log.error(String.format(LOG_EXCEPTION_FORMAT, code, ex.getMessage()));
        return ResultUtil.error(code, ex.getMessage(), getNewJwtToken.getNewJWTToken(request));
    }

    /**
     * 格式化
     *
     * @param code
     * @param msg
     * @param request
     * @param <T>
     * @return
     */
    private <T extends Throwable> ResponseBean customExceptionFormat(Integer code, String msg, HttpServletRequest request) {
        log.error(String.format(LOG_EXCEPTION_FORMAT, code, msg));
        return ResultUtil.error(code, msg, getNewJwtToken.getNewJWTToken(request));
    }
}
