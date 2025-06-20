package com.code.usermanagerservice.common;

import lombok.Data;
/**
 * 统一返回结果
 */
@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }
    public static <T> Result<T> fail(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.FAIL.getCode());
        result.setMsg(ResultCode.FAIL.getMsg());
        result.setData(data);
        return result;
    }

}
