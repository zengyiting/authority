package com.code.usermanagerservice.enums;

public enum TimeOutEnum {
    CODE_TIME_OUT("code time out(minute)", 5);
    private final  String name;
    private final  int timeOut;
    TimeOutEnum(String name, int timeOut) {
        this.name = name;
        this.timeOut = timeOut;
    }
    public String getName() {
        return name;
    }
    public int getTimeOut() {
        return timeOut;
    }

}
