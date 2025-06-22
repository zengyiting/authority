package com.code.usermanagerservice.model.dto;

import lombok.Data;

@Data
public class UserInfoDto {
    /**
     * 用户id
     */
    private Long userId;

   /**
     * 用户名
     */
    private String username;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态（1:正常 0:禁用）
     */
    private Integer status;

}
