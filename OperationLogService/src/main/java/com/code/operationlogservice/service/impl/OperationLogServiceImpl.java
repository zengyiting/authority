package com.code.operationlogservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.code.operationlogservice.service.OperationLogService;
import com.code.operationlogservice.model.entity.OperationLog;
import com.code.operationlogservice.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;

/**
* @author 寒春初一
* @description 针对表【operation_logs】的数据库操作Service实现
* @createDate 2025-06-16 00:00:17
*/
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog>
    implements OperationLogService {

}




