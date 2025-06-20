package com.code.usermanagerservice.enums;

public enum ActionEnum {
    REGISTER("register");
    private String action;
    ActionEnum(String action) {
        this.action = action;
    }
    public String getAction() {
        return action;
    }


}
