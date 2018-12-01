package com.coco.winter.login.service.Impl;

import com.coco.whale.common.utils.MD5Tools;
import com.coco.whale.login.dao.UserDao;
import com.coco.whale.login.entity.UserInfo;
import com.coco.whale.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
