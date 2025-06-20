package com.code.operationlogservice.enums;

public enum ConfigEnum {
    WORKED_ID("workedId", "1"),
    PASSWORD_SALT("passwordSalt", "1025"),
    DATACENTER_ID("datacenterId", "1");
    private final String text;
    private final String value;


    ConfigEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

}
