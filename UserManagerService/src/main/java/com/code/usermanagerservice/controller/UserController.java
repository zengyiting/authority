package com.code.usermanagerservice.controller;

import com.code.usermanagerservice.common.Result;
import com.code.usermanagerservice.model.dto.UserRegisterRequest;
import com.code.usermanagerservice.model.dto.UserSmsRequest;
import com.code.usermanagerservice.service.UsersService;
import com.code.usermanagerservice.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UsersService usersService;
    //登录
    @PostMapping("/sms")
    public Result<String> sendSMS(@Valid @RequestBody UserSmsRequest request) {
        usersService.sendSMS(request);

        return Result.success("发送成功");
    }
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody UserRegisterRequest registerRequest, HttpServletRequest request){
        String ip = IpUtil.getClientIp(request);
        registerRequest.setIp(ip);
        usersService.register(registerRequest);
        log.info("用户{}注册成功，来自{}",registerRequest.getUsername(),ip);
        return Result.success("注册成功");
    }


}
