package com.coco.winter.login.service;

import com.coco.winter.login.entity.UserInfo;
import org.apache.catalina.User;

import java.util.Map;

/**
 * @ClassName UserService
 * @Description 用户service接口
 * @Author like
 * @Data 2018/11/10 14:17
 * @Version 1.0
 **/

public interface UserService {

    /**
     * 通过用户名查找用户信息
     * @param userName
     * @return
     */
    UserInfo fingByUserName(String userName);

    /**
     * 注册用户
     * @param userName
     * @param passWord
     * @return
     */
    boolean insertUser(String userName, String passWord);

    /**
     * 用户登录
     * @param map
     * @return
     * @throws Exception
     */
    UserInfo userLogin(Map<String,Object> map) throws Exception;
}
