package com.coco.winter.common.entity;

import lombok.Data;

/**
 * @ClassName Identity
 * @Description JwtToken的载荷体
 * @Author like
 * @Data 2018/11/10 16:57
 * @Version 1.0
 **/

@Data
public class Identity {

    /**
     * 对应user的id
     */
    private String id;
    /**
     * 签发者
     */
    private String issuer;
    /**
     * 角色
     */
    private String role;
    /**
     * 权限
     */
    private String perms;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 有效时长，单位毫秒
     */
    private Long duration;

}
