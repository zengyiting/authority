package com.code.usermanagerservice.model.enums;

import javax.xml.crypto.Data;

public enum ConfigEnum {
    WORKED_ID("workedId", "1"),
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
