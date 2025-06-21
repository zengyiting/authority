package com.code.usermanagerservice.model.dto;

import lombok.Data;

@Data
public class UserLoginResponse {
    private String accessToken;

    private String refreshToken;

    private Long expiresIn;

    private Long userId;

    private String name;
}
