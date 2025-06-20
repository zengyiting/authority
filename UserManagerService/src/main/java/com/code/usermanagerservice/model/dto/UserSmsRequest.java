package com.code.usermanagerservice.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class UserSmsRequest {
    @NotEmpty(message = "手机号不能为空")
    @Length(min = 11, max = 11, message = "手机号长度为11位")
    private String phone;
}
