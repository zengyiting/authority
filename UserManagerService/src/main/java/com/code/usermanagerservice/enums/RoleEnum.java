package com.code.usermanagerservice.enums;

public enum RoleEnum {
    SUPER_ADMIN(1),
    USER(2),
    ADMIN(3);

    private final int code;
    RoleEnum(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

}
