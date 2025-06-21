package com.code.usermanagerservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.code.usermanagerservice.model.dto.*;
import com.code.usermanagerservice.model.entity.User;

import java.util.List;


/**
* @author 寒春初一
* @description 针对表【users_0(用户表)】的数据库操作Service
* @createDate 2025-06-14 20:58:50
*/
public interface UsersService extends IService<User> {

    public  void sendSMS(UserSmsRequest userSmsRequest);
    public  void register(UserRegisterRequest userRegisterRequest);
    public UserLoginResponse login(UserLoginRequest userLoginRequest);
    public RefreshTokenResponse refresh(String refreshToken);
    public List<UserResponse> getUserList(Long userId);


}
