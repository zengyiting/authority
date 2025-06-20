package com.code.operationlogservice.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class LogSaveRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;

    /**
     * 操作类型,  如update_user
     */
    private String action;

    /**
     * ip地址,支持ipv6
     */
    private String ip;

    /**
     * 操作详情
     */
    private String detail;

    /**
     * 创建时间
     */
    private Date gmtCreate;
}
