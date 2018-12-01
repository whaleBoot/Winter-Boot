package com.coco.winter.login.controller;

import com.coco.whale.common.entity.ResponseBean;
import com.coco.whale.common.utils.JacksonJsonUtil;
import com.coco.whale.common.utils.ResultUtil;
import com.coco.whale.login.entity.UserInfo;
import com.coco.whale.login.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户信息注册
     *
     * @param param
     * @return
     */
    @ApiModelProperty
    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "用户注册")
    public ResponseBean registerUser(@RequestBody @ApiParam(value = "用户注册信息") Map<String, Object> param, BindingResult bindingResult) {
        Map<String, Object> token = new HashMap<String, Object>();
        token.put("jwtToken", "");
        String requestBody = String.valueOf(param.get("requestBody"));
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map = JacksonJsonUtil.json2map(requestBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String userName = String.valueOf(map.get("userName"));
        String passWord = String.valueOf(map.get("passWord"));

        UserInfo userInfo = userService.fingByUserName(userName);
        if (userInfo != null) {
            return ResultUtil.error(-1, "用户名已存在，请重新输入！", null, token);
        }
        boolean bo = userService.insertUser(userName, passWord);
        if (bo) {
            return ResultUtil.success("注册成功！", null, token);

        } else {
            return ResultUtil.error(-1, "注册失败！", null, token);
        }
    }

    @PostMapping("/login")
    public ResponseBean login(){

        return null;
    }
}
