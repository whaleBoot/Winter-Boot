package com.coco.winter.login.service.Impl;

import com.coco.winter.exception.CustomException;
import com.coco.winter.login.dao.UserDao;
import com.coco.winter.login.entity.UserInfo;
import com.coco.winter.login.service.UserService;
import com.coco.winter.utils.MD5Tools;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName UserServiceImpl
 * @Description 用户service实现类
 * @Author like
 * @Data 2018/11/10 14:17
 * @Version 1.0
 **/

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserInfo fingByUserName(String userName) {
        return userDao.findByUsername(userName);

    }

    @Override
    public boolean insertUser(String userName, String passWord) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(userName);
        userInfo.setPassword(MD5Tools.generate(passWord));
        try {
            userDao.save(userInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public UserInfo userLogin(Map<String, Object> map)throws Exception {
        String userName = String.valueOf(map.get("userName"));
        String passWord = String.valueOf(map.get("passWord"));
        UserInfo userInfo = userDao.findByUsername(userName);
        if (userInfo == null) {
            throw new CustomException(-1, "用户名或密码错误！");
        }
        String passWordMD5 = userInfo.getPassword();
        if (MD5Tools.verify(passWord, passWordMD5)) {
            return userInfo;
        } else {
            throw new CustomException(-1, "用户名或密码错误！");

        }
    }
}
