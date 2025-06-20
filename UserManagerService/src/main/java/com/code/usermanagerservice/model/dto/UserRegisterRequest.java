package com.code.usermanagerservice.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
@Data
public class UserRegisterRequest {
    @NotEmpty(message = "手机号不能为空")
    @Length(min = 11, max = 11, message = "手机号长度为11位")
    private String phone;
    @NotEmpty(message = "用户名不能为空")
    private String username;
    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度为6-20位")
    private String password;
    @NotEmpty(message = "验证码不能为空")
    private String code;
    private String ip;
}
