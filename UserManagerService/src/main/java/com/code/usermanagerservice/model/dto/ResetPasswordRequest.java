package com.code.usermanagerservice.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度为6-20位")
    private String password;
    private String ip;
}
