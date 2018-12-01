package com.coco.winter.realm;


import com.coco.winter.common.entity.Identity;
import com.coco.winter.common.entity.JwtToken;
import com.coco.winter.login.entity.SysPermission;
import com.coco.winter.login.entity.SysRole;
import com.coco.winter.login.entity.UserInfo;
import com.coco.winter.login.service.UserService;
import com.coco.winter.utils.JWTTokenUtil;
import com.coco.winter.utils.MD5Tools;
import com.coco.winter.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName MyShiroRealm
 * @Description 自定义用户权限
 * @Author like
 * @Data 2018/11/10 14:13
 * @Version 1.0
 **/
@Slf4j
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 当使用自定义token时，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 授权 权限管理
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        UserInfo userInfo = (UserInfo) principalCollection.getPrimaryPrincipal();
        for (SysRole role : userInfo.getRoleList()) {
            authorizationInfo.addRole(role.getRole());
            for (SysPermission p : role.getPermissions()) {
                authorizationInfo.addStringPermission(p.getPermission());
            }
        }
        return authorizationInfo;
    }

    /**
     * 主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("MyShiroRealm.doGetAuthenticationInfo()");
        //把AuthenticationToken转换为JwtToken
        JwtToken jwtToken = (JwtToken) authenticationToken;
        //从JwtToken中获取username
        String username = jwtToken.getUsername();
        // 获取 密码，字符数组需要转型为 String
        String password = jwtToken.getPassword();
        String userId = jwtToken.getUserId();

        int status = jwtToken.getStatus();//0:web 1:mobile
        if (status == 0) {
            //获取shiro session信息
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession();
            //将用户登录的状态存入session中   用于CORSAuthenticationFilter 中token的辨别  移动端未放入header中  所以用此方式处理
            session.setAttribute("loginStatus", status);
            //将用户名存入session中   保存系统日志的时候用
            session.setAttribute("loginName", username);
        } else {
            redisUtil.set(username + ":status", status);
            redisUtil.set(username + ":loginName", username);
        }

        if (status == 0) {
            //web端  此处不需要对账号密码进行验证，已经在service验证过
            //生成token  并保存到redis 密钥也保存到redis
            createWebToken(SecurityUtils.getSubject().getSession(), userId, username, password);
            // 身份认证成功，返回 SimpleAuthenticationInfo 对象  参数1：用户名 参数2：密码参数3：当前 Realm 的名称
            return new SimpleAuthenticationInfo(username, password, this.getName());

        } else {
            //移动端时登录
            //生成token  并保存token
            createMobileToken(userId, username, password);
            // 身份认证成功，返回 SimpleAuthenticationInfo 对象  参数1：用户名 参数2：密码参数3：当前 Realm 的名称
            return new SimpleAuthenticationInfo(username, password, this.getName());
        }
    }


    /**
     * 保存并创建Web端token
     *
     * @param userId
     * @param userName
     * @param password
     */
    private void createWebToken(Session session, String userId, String userName, String password) {
        //生成redis中的存放token的key以及载荷的key和密码密钥的key，生成规则为pansoft:XX:+session.getId()(session.id为唯一值)
        String tokenKey = "pansoft:tokenKey:" + session.getId();
        String passwordKey = "pansoft:passwordKey:" + session.getId();
        //生成identity实体,jwtToken的载荷信息
        Identity identity = new Identity();
        identity.setId(userId);
        identity.setIssuer("admin");
        identity.setUserName(userName);
        identity.setRole("111");
        identity.setDuration(1800000L);
        //保存token到redis  此处将加密后的用户密码加上前缀pansoft作为token的密钥 并且将此密钥存入到redis中  Key值为pansoft:passwordKey:加上session.getId();
        String passwordValue = "pansoft" + password;
        //web端token有效期为30分钟   1800秒     密钥有效期为2天   172800秒
        redisUtil.set(tokenKey, JWTTokenUtil.createToken(identity, passwordValue), 1800L);
        redisUtil.set(passwordKey, passwordValue, 172800L);
    }

    /**
     * 保存并创建移动端token
     *
     * @param userId
     * @param userName
     * @param password
     */
    private void createMobileToken(String userId, String userName, String password) {
        //生成redis中的存放token的key以及载荷的key和密码密钥的key，生成规则为pansoft:XX:+session.getId()(session.id为唯一值)
        String tokenKey = "pansoft:tokenKey:" + userName;
        String passwordKey = "pansoft:passwordKey:" + userName;
        //生成identity实体,jwtToken的载荷信息
        Identity identity = new Identity();
        identity.setId(userId);
        identity.setIssuer("admin");
        identity.setUserName(userName);
        identity.setRole("111");
        identity.setDuration(1800000L);
        //保存token到redis  此处将加密后的用户密码加上前缀pansoft作为token的密钥 并且将此密钥存入到redis中  Key值为pansoft:passwordKey:加上session.getId();
        String passwordValue = "pansoft" + password;
        // 移动端token有效期为一个月31天     2678400秒
        redisUtil.set(tokenKey, JWTTokenUtil.createToken(identity, passwordValue), 2678400L);
        redisUtil.set(passwordKey, passwordValue, 2678400L);
    }


}
