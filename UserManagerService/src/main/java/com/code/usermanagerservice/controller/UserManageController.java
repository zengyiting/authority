package com.code.usermanagerservice.controller;

import com.code.usermanagerservice.common.Result;
import com.code.usermanagerservice.model.dto.UserResponse;
import com.code.usermanagerservice.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RequestMapping("/api/u1/valid")
@RestController
public class UserManageController {
    @Autowired
    private UsersService usersService;
    //查询用户信息
    @GetMapping("/{userId}")
    public Result<List<UserResponse>> getUsers(@PathVariable Long userId) {

        List<UserResponse> userResponseList = usersService.getUserList(userId);
        return Result.success(userResponseList);

    }
}
