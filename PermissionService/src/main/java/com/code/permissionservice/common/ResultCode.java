package com.code.permissionservice.common;
/**
 * 统一返回结果状态码
 */
public enum ResultCode {
    SUCCESS(200, "成功"),

    FAIL(400, "失败"),

    NOT_FOUND(404, "未找到"),

    UNAUTHORIZED(401, "未授权"),

    FORBIDDEN(403,"禁止访问"),

    INTERNAL_SERVER_ERROR(500, "服务器内部错误");

    private final int code;
    private final String msg;
    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}
