package com.code.gateway.enums;

public enum ErrorEnum {
    REGISTER_ERROR("注册失败, 用户已存在"),
    MYSQL_ERROR("数据库错误"),
    BIND_ERROR("绑定失败"),
    CODE_ERROR("注册失败,验证码错误");

    private final String message;

    ErrorEnum(String message) {

        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
