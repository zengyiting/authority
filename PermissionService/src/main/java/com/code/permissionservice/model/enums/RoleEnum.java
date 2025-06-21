package com.code.permissionservice.model.enums;

public enum RoleEnum {
    USER(1),
    ADMIN(2),
    SUPER_ADMIN(3);
    private final int code;
    RoleEnum(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

}
