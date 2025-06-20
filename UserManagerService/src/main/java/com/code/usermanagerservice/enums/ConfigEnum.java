package com.code.usermanagerservice.enums;

public enum ConfigEnum {
    WORKED_ID("workedId", "1"),
    PASSWORD_SALT("passwordSalt", "1025"),
    DATACENTER_ID("datacenterId", "1"),
    EXCHANGE_NAME("exchangeName", "exchange_user"),
    QUEUE_NAME("queueName", "queue_user"),
    ROUTING_KEY("routingKey", "action_log");
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
