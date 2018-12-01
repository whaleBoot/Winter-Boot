package com.coco.winter.common.entity;

import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @ClassName JWTToken
 * @Description 现AuthenticationToken，不使用shiro的UsernamePasswordtoken,内容可以后续使用到再添加
 * @Author like
 * @Data 2018/11/10 15:10
 * @Version 1.0
 **/

@Data
public class JwtToken implements AuthenticationToken {

    private static final long serialVersionUID = 8684418752076285487L;

    private String username;

    private String password;

    private String host;

    /**用于区分用户来源 0：web用户；1：移动用户**/
    private int status;

    public JwtToken(String username, String password, String host, int status) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.status = status;
    }

    @Override
    public Object getPrincipal() {
        return this.getUsername();
    }

    @Override
    public Object getCredentials() {
        return this.getPassword();
    }
}
