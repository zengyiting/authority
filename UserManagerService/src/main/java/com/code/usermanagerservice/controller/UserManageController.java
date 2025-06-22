package com.code.usermanagerservice.controller;

import com.code.usermanagerservice.common.BusinessException;
import com.code.usermanagerservice.common.PageResult;
import com.code.usermanagerservice.common.Result;
import com.code.usermanagerservice.enums.ErrorEnum;
import com.code.usermanagerservice.model.dto.ResetPasswordRequest;
import com.code.usermanagerservice.model.dto.UserInfoDto;
import com.code.usermanagerservice.model.dto.UserPageRequest;
import com.code.usermanagerservice.model.dto.UserResponse;
import com.code.usermanagerservice.service.UsersService;
import com.code.usermanagerservice.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequestMapping("/api/u1/valid")
@RestController
public class UserManageController {
    @Autowired
    private UsersService usersService;
    @GetMapping("/{userId}")
    public Result<UserInfoDto> getUsers(@PathVariable Long userId,HttpServletRequest request) {
        String myUserId = request.getHeader("X-User-Id");
        UserInfoDto userInfoDtoList = usersService.getUserInfoList(Long.valueOf(myUserId),userId);
        return Result.success(userInfoDtoList);

    }
    @GetMapping("/page/users")
    public Result<PageResult<UserInfoDto>> getPageUsers(@RequestBody UserPageRequest userPageRequest, HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        PageResult<UserInfoDto> userResponseList = usersService.getUserList(userPageRequest, Long.valueOf(userId));
        return Result.success(userResponseList);

    }
    @PutMapping("/update/{userId}")
    public Result<String> updateUser(@PathVariable Long userId, @RequestBody UserInfoDto userInfoDto,HttpServletRequest request) {
        String myUserId = request.getHeader("X-User-Id");
        String ip = IpUtil.getClientIp(request);
        userInfoDto.setUserId(userId);
        usersService.updateUser(Long.valueOf(myUserId), userInfoDto, ip);
        return Result.success("修改成功");
    }
    @PostMapping("/reset/password")
    public Result<String> resetPassword(HttpServletRequest request, @Valid @RequestBody ResetPasswordRequest resetPasswordRequest){
        String ip = IpUtil.getClientIp(request);
        String userId = request.getHeader("X-User-Id");
        resetPasswordRequest.setIp(ip);
        usersService.resetPassword(Long.valueOf(userId),resetPasswordRequest);
        return Result.success("密码重置成功");
    }

}
