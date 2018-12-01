package com.coco.winter.login.controller;


import com.coco.winter.common.entity.JwtToken;
import com.coco.winter.common.entity.ResponseBean;
import com.coco.winter.common.serviceImpl.GetNewJwtToken;
import com.coco.winter.login.entity.UserInfo;
import com.coco.winter.login.service.UserService;
import com.coco.winter.utils.JacksonJsonUtil;
import com.coco.winter.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UserController
 * @Description 用户 Controller
 * @Author like
 * @Data 2018/11/10 15:37
 * @Version 1.0
 **/

@Api(value = "UserController", description = "用户登陆、注册、信息维护")
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GetNewJwtToken getNewJwtToken;

    /**
     * 用户信息注册
     *
     * @param param 请求体
     * @return
     */
    @ApiModelProperty
    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "用户注册")
    public ResponseBean registerUser(@RequestBody @ApiParam(value = "用户注册信息") Map<String, Object> param) throws Exception {
        Map<String, Object> token = new HashMap<String, Object>();
        token.put("jwtToken", "");
        String requestBody = String.valueOf(param.get("requestBody"));
        Map<String, Object> map = new HashMap<String, Object>();

        map = JacksonJsonUtil.json2map(requestBody);

        String userName = String.valueOf(map.get("userName"));
        String passWord = String.valueOf(map.get("passWord"));

        UserInfo userInfo = userService.fingByUserName(userName);
        if (userInfo != null) {
            return ResultUtil.error(-1, "用户名已存在，请重新输入！", token);
        }
        boolean bo = userService.insertUser(userName, passWord);
        if (bo) {
            return ResultUtil.success("注册成功！", null, token);

        } else {
            return ResultUtil.error(-1, "注册失败！", null, token);
        }
    }

    /**
     * 用户登录
     *
     * @param request
     * @param param
     * @return
     * @throws Exception
     */
    @ApiModelProperty
    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "用户登录")
    public ResponseBean login(@ApiParam(value = "HttpServletRequest") HttpServletRequest request,
                              @RequestBody @ApiParam(value = "用户登录信息") Map<String, Object> param) throws Exception {
        Map<String, Object> token = new HashMap<String, Object>();
        token.put("jwtToken", "");
        String requestBody = String.valueOf(param.get("requestBody"));
        Map<String, Object> requestMap = JacksonJsonUtil.json2map(requestBody);
        UserInfo userInfo = userService.userLogin(requestMap);
        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("userId", userInfo.getUid());
        responseMap.put("userName", userInfo.getUsername());
        responseMap.put("name", userInfo.getName());
        return userAndPwdLogin(request, requestMap, responseMap, token);
    }

    /**
     * 用户密码shiro管理
     *
     * @param request
     * @param requestMap
     * @param userInfo
     * @param token
     * @return
     * @throws Exception
     */
    protected ResponseBean userAndPwdLogin(HttpServletRequest request, Map<String, Object> requestMap,
                                           HashMap<String, Object> responseMap, Map<String, Object> token) throws Exception {
        String userName = String.valueOf(requestMap.get("userName"));
        String passWord = String.valueOf(requestMap.get("passWord"));

        JwtToken jwtToken = new JwtToken(String.valueOf(responseMap.get("userId")), userName, passWord, 0);
        SecurityUtils.getSubject().login(jwtToken);
        log.info("web端登陆成功");
        return ResultUtil.success("登录成功！", responseMap, getNewJwtToken.getNewJWTToken(request));
    }
}
