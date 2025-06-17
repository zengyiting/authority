package com.code.operationlogservice.mapper;

import com.code.operationlogservice.model.entity.OperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 寒春初一
* @description 针对表【operation_logs】的数据库操作Mapper
* @createDate 2025-06-16 00:00:17
* @Entity com.code.operationlogservice.model.entity.OperationLog
*/
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

}




