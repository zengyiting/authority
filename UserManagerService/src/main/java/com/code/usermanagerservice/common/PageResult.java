package com.code.usermanagerservice.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.checkerframework.checker.units.qual.K;

import java.util.List;
//by ai
@Data
public class PageResult<T> {
    private List<T> records;
    private long total;
    private long pageNum;
    private long pageSize;
    public PageResult() {
    }


}
