package com.code.usermanagerservice.enums;

public enum ErrorEnum {
    REGISTER_ERROR("注册失败, 用户已存在"),
    MYSQL_ERROR("数据库错误"),
    BIND_ERROR("绑定失败"),
    CODE_ERROR("注册失败,验证码错误"),
    USER_PASSWORD_ERROR("用户密码错误"),
    USER_DISABLED("用户被禁用"),
    GET_ROLE_ERROR("获取用户角色失败"),
    USER_NOT_EXIST("用户不存在");

    private final String message;

    ErrorEnum(String message) {

        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
