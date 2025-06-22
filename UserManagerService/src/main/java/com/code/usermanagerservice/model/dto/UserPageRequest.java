package com.code.usermanagerservice.model.dto;

import lombok.Data;

@Data
public class UserPageRequest {
    private Integer pageNum = 1; // 当前页码
    private Integer pageSize = 10;// 每页数量
}
