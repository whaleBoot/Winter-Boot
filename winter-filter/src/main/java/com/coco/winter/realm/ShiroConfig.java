package com.coco.winter.realm;


import com.coco.winter.filter.JWTFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ClassName ShiroConfig
 * @Description Shiro 配置
 * @Author like
 * @Data 2018/11/10 14:09
 * @Version 1.0
 **/

@Configuration
@Slf4j
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        log.info("ShiroConfiguration.shirFilter()");
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        factoryBean.setSecurityManager(securityManager);

        // 添加自己的过滤器并且取名为jwt
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("authc", new JWTFilter());
        factoryBean.setFilters(filterMap);
        factoryBean.setSecurityManager(securityManager);

        // 自定义url规则 配置不会被拦截的链接 顺序判断
        Map<String, String> filterRuleMap = new LinkedHashMap<String, String>();
        filterRuleMap.put("/druid/**", "anon");
        filterRuleMap.put("/user/register", "anon");
        filterRuleMap.put("/user/login", "anon");
        filterRuleMap.put("/favicon.ico", "anon");

        // swagger
        filterRuleMap.put("/swagger-ui.html", "anon");
        filterRuleMap.put("/swagger-resources/**", "anon");
        filterRuleMap.put("/v2/api-docs", "anon");
        filterRuleMap.put("/webjars/springfox-swagger-ui/**", "anon");
        filterRuleMap.put("/static/**", "anon");
        //过滤链定义，从上向下顺序执行，一般将/**放在最下边
        // authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问
        filterRuleMap.put("/**", "authc");

//        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
//        factoryBean.setLoginUrl("/login");
//        // 登录成功后要跳转的链接
//        factoryBean.setSuccessUrl("/index");
//        //未授权界面;
//        factoryBean.setUnauthorizedUrl("/403");
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return factoryBean;
    }

    @Bean
    public UserRealm myShiroRealm() {
        UserRealm userRealm = new UserRealm();
        return userRealm;
    }


    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myShiroRealm());
        return securityManager;
    }
}
