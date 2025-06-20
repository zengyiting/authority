package com.code.usermanagerservice.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public Result<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        String msg = e.getMessage();
        log.error("系统错误:" + msg);
        return Result.fail(msg);
    }
    @ExceptionHandler(BusinessException.class)
    public Result<?> serviceExceptionHandler(BusinessException e) {
        log.error("ServiceException", e);
        return Result.fail(e.getMessage());

    }
}
