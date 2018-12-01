package com.coco.winter.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 注解配置使用@Component 一个可以，  或者是使用 @WebFilter和@Configuration 两个注解 （百度好像是jar包和war包得区别jar包时一个@WebFilter配置即可）
 */
@WebFilter(filterName="corsFilter",urlPatterns="/*")
@Configuration
@Order(1)
public class SimpleCORSFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request =(HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String origin = request.getHeader("origin");// 获取源站
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "1800"); //预检请求返回的结果可以被缓存的时间  单位是秒
        response.setHeader("Access-Control-Allow-Credentials", "true"); //带Cookie的跨域ajax请求
        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,Content-Type,JWTToken,ticket");
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }
}
