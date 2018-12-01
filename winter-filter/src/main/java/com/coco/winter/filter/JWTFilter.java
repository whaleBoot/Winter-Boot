package com.coco.winter.filter;


import com.coco.winter.common.entity.Identity;
import com.coco.winter.utils.JWTTokenUtil;
import com.coco.winter.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName JWTFilter
 * @Description 自定义JWTFilter
 * @Author like
 * @Data 2018/11/12 9:06
 * @Version 1.0
 **/

@Slf4j
public class JWTFilter extends BasicHttpAuthenticationFilter {

    /**
     * 表示是否允许访问 ，如果允许访问返回true，否则false；
     * @param request
     * @param response
     * @param obj
     * @return
     * @throws Exception
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object obj)  {
        //对于OPTIONS请求，一直返回允许访问
        if(request instanceof HttpServletRequest){
            if(((HttpServletRequest) request).getMethod().toUpperCase().equals("OPTIONS")){
                log.info("Ajax 登录认证跨域请求Options预检通过");
                return true;
            }
        }
        return false;
    }

    /**
     * onAccessDenied：表示当访问拒绝时是否已经处理了；如果返回 true 表示需要继续处理；如果返回 false 表示该拦截器实例已经处理了，将直接返回即可。
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        //因注入redisUtil 为null 所以改用从上下文中获取，此问题只有在filter中才会出现，暂时用此方式解决，其他方式试了无效
        ServletContext context = request.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
        RedisUtil redisUtil = ctx.getBean(RedisUtil.class);

        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        res.setContentType("application/json;charset=UTF-8");

        //设置一个固定的token 在不登录的情况下 也可以访问接口获取数据信息
        String token = req.getParameter("token");
        if("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNTI3NzUzOTI5LCJzdWIiOiIxLGFkbWluLDExMSIsImlzcyI6ImFkbWluIiwiZXhwIjoxNTI3NzU1NzI5fQ.eTGWB0OzXL5libsDXr5_9Gfj8XurKA1AvZO_xVTGUQs".equals(token)){
            return true;
        }
        //获取移动端或者web端的用户名，用户名用于redis的key
        String username = "";
        //获取前台传的JWTToken的值，用来验证是否为安全的访问
        String headerToken = "";
        String loginStatus = "";
        if(StringUtils.isNotBlank(req.getParameter("useraccount"))){
            headerToken = token;
            username = req.getParameter("useraccount");
            loginStatus = "1";
        }else {
            headerToken = req.getHeader("JWTToken");
            Subject subject = getSubject(request, response);
            Object obj=subject.getPrincipal();
            log.info("登录权限------------- {}",obj);
            if(null == obj){
                try {
                    res.getWriter().write("{\"code\":1,\"msg\":\"未登录请进行登录！\",\"data\":\"\",\"token\":{\"jwtToekn\":\"\"}}");
                    res.getWriter().flush();
                    res.getWriter().close();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //如果已经登录，获取当前用户的session信息
            Session session = subject.getSession();
            username = String.valueOf(session.getId());
            loginStatus = String.valueOf(session.getAttribute("loginStatus"));
        }

        try {
            //通过tokenKey值  从redis中获取token
            String redisToken  = String.valueOf(redisUtil.get("pansoft:tokenKey:"+username));
            //通过用户密码key 从redis中获取token的密钥
            String redisPwdValue = String.valueOf(redisUtil.get("pansoft:passwordKey:"+username));
            //判断前台传的token和后台redis中获取的token都不为空 才能进行执行以下方法
            if(StringUtils.isBlank(headerToken) || StringUtils.isBlank(redisToken)){
                res.getWriter().write("{\"code\":1,\"msg\":\"未传递有效的token！\",\"data\":\"\",\"token\":{\"jwtToekn\":\"\"}}");
                res.getWriter().flush();
                res.getWriter().close();
                return false;
            }
            //如果redis中token和用户传过来的token进行对比，如果一致，再使用密钥进行比对
            if(redisToken.equals(headerToken)){
                //从token中获得信息   此处密钥为用户的密码加上前缀pansoft组成
                Identity identity = JWTTokenUtil.parseToken(headerToken, redisPwdValue);
                //如果identity 不为空，说明token在有效期之内，然后重新生成一个新的token，放到redis缓存中，
                //然后前台每次进行请求时，都将新生成的以当前时间为起点有效期半个小时的token通过responseBean传回前台
                if(null != identity){
                    //将旧的token存到redis并且设置30秒的过期时间，此处是为了并发设置，即同一客户端，在同一时间有多个不同的请求发到后台
                    //为了防止token一直换新的导致后面的请求失效
                    redisUtil.set("pansoft:oldTokenKey:"+username, headerToken,30L);
                    if(!"1".equals(loginStatus)){ //web端  设置token有效期为  30分钟   1800秒
                        redisUtil.set("pansoft:tokenKey:"+username, JWTTokenUtil.createToken(identity, redisPwdValue),1800L);
                        return true;
                    }
                    //移动端    设置token有效期为 1个月      31天    2678400秒
                    redisUtil.set("pansoft:tokenKey:"+username, JWTTokenUtil.createToken(identity, redisPwdValue),2678400L);
                    return true;

                }
            }
            /**
             * 处理当同一客户端并发请求时 ，导致第一次请求可以通过，后面请求不能通过的情况，首先检查redis中存的旧token是否过期
             * 如果旧token不过期，则检查前台发送的token与旧token是否一致，一致则解析token并生成新的token存到redis中
             */
            if(redisUtil.getExpire("pansoft:oldTokenKey:"+username) > 0){
                String oldToken = String.valueOf(redisUtil.get("pansoft:oldTokenKey:"+username));
                if(headerToken.equals(oldToken)){
                    Identity identity = JWTTokenUtil.parseToken(headerToken, redisPwdValue);
                    if(!"1".equals(loginStatus)){//web端  设置token有效期为  30分钟   1800秒
                        redisUtil.set("pansoft:tokenKey:"+username, JWTTokenUtil.createToken(identity, redisPwdValue),1800L);
                        return true;
                    }
                    //移动端    设置token有效期为 1个月      31天    2678400秒
                    redisUtil.set("pansoft:tokenKey:"+username, JWTTokenUtil.createToken(identity, redisPwdValue),2678400L);
                    return true;
                }
            }
            res.getWriter().write("{\"code\":1,\"msg\":\"token已失效！\",\"data\":\"\",\"token\":{\"jwtToekn\":\"\"}}");
            res.getWriter().flush();
            res.getWriter().close();
            return false;
        } catch (Exception e) {
            log.error("**登录的authcFilter出现错误！{}",e);
            try {
                res.getWriter().write("{\"code\":1,\"msg\":\"拦截器异常！\",\"data\":\"\",\"token\":{\"jwtToekn\":\"\"}}");
                res.getWriter().flush();
                res.getWriter().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }


}
