package com.code.usermanagerservice.model.dto;

import lombok.Data;

@Data
public class RefreshTokenResponse {
    private String accessToken;

    private String refreshToken;

    private Long expiresIn;
}
