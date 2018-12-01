package com.coco.winter.login.dao;

import com.coco.whale.login.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName UserDao
 * @Description TODO
 * @Author like
 * @Data 2018/11/10 14:15
 * @Version 1.0
 **/

public interface UserDao  extends JpaRepository<UserInfo,Integer> {

    /**
     * 通过用户名查找用户信息
     * @param userName
     * @return
     */
    public UserInfo findByUsername(String userName);

}
