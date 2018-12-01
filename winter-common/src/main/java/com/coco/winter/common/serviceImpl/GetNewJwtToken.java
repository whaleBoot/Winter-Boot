package com.coco.winter.common.serviceImpl;

import com.coco.winter.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName GetNewJwtToken
 * @Description  获取新的JwtToken
 * @Author like
 * @Data 2018/11/16 17:18
 * @Version 1.0
 **/
@Service
public class GetNewJwtToken {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取当前用户token未过期时，新创建的token值
     * @return
     */
    public Map<String, Object> getNewJWTToken(HttpServletRequest request) {
        String username = "";
        if(StringUtils.isNotBlank(request.getParameter("useraccount"))){
            username = request.getParameter("useraccount");
        }else {
            Subject subject = SecurityUtils.getSubject();
            //为了方便测试，增加不登录也可以测试后台方法
            if(null == subject.getPrincipal()){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("jwtToken", "");
                return map;
            }
            username = String.valueOf(subject.getSession().getId());
        }
        String redisToken = String.valueOf(redisUtil.get("pansoft:tokenKey:"+username));
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("jwtToken", redisToken);
        return map;
    }
}
