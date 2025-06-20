package com.code.operationlogservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.code.operationlogservice.common.Constant;
import com.code.operationlogservice.enums.ConfigEnum;
import com.code.operationlogservice.model.dto.LogSaveRequest;
import com.code.operationlogservice.service.OperationLogService;
import com.code.operationlogservice.model.entity.OperationLog;
import com.code.operationlogservice.mapper.OperationLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 寒春初一
* @description 针对表【operation_logs】的数据库操作Service实现
* @createDate 2025-06-16 00:00:17
*/
@Slf4j
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog>
    implements OperationLogService {
    @Autowired
    private OperationLogMapper operationLogMapper;
    @RabbitListener(queues = Constant.QUEUE_NAME)
    public void saveLog(LogSaveRequest logSaveRequest) {

        OperationLog operationLog = new OperationLog();
        operationLog.setUserId(logSaveRequest.getUserId());
        operationLog.setAction(logSaveRequest.getAction());
        operationLog.setIp(logSaveRequest.getIp());
        operationLog.setDetail(logSaveRequest.getDetail());
        this.save(operationLog);
        log.info("保存日志成功:", operationLog);
    }
}




