package com.code.usermanagerservice.enums;

public enum TimeOutEnum {
    CODE_TIME_OUT("code time out(minute)", 5L),
    ACCESS_TOKEN_TIMEOUT("access token timeout (minute)", 30L),   // 30分钟
    REFRESH_TOKEN_TIMEOUT("refresh token timeout (minute)", 7L * 24 * 60); // 7天;
    private final  String name;
    private final  Long timeOut;
    TimeOutEnum(String name, Long timeOut) {
        this.name = name;
        this.timeOut = timeOut;
    }
    public String getName() {
        return name;
    }
    public Long getTimeOut() {
        return timeOut;
    }

}
