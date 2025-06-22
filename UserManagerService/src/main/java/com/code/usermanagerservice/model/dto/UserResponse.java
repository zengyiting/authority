package com.code.usermanagerservice.model.dto;

import lombok.Data;

@Data
public class UserResponse {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;
}
