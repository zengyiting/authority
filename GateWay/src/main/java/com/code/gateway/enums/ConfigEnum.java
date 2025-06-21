package com.code.gateway.enums;

public enum ConfigEnum {
    WORKED_ID("workedId", "1"),
    PASSWORD_SALT("passwordSalt", "1025"),
    DATACENTER_ID("datacenterId", "1"),
    EXCHANGE_NAME("exchangeName", "exchange_user"),
    QUEUE_NAME("queueName", "queue_user"),
    REGISTER_REDIS_KEY("registerRedisKey", "register:"),
    TOKEN_SECRET_KEY("tokenSecretKey", "zyta"),
    REFRESH_TOKEN_KEY("refreshTokenKey","refresh:"),
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
