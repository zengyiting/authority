package com.code.operationlogservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.code.operationlogservice.common.Constant;
import com.code.operationlogservice.enums.ConfigEnum;
import com.code.operationlogservice.model.dto.LogSaveRequest;
import com.code.operationlogservice.service.OperationLogService;
import com.code.operationlogservice.model.entity.OperationLog;
import com.code.operationlogservice.mapper.OperationLogMapper;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
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

    @RabbitListener(queues = Constant.QUEUE_NAME, ackMode = "MANUAL")
    public void saveLog(LogSaveRequest logSaveRequest, Channel channel, Message message) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setUserId(logSaveRequest.getUserId());
            operationLog.setAction(logSaveRequest.getAction());
            operationLog.setIp(logSaveRequest.getIp());
            operationLog.setDetail(logSaveRequest.getDetail());
            this.save(operationLog);
            log.info("保存日志成功: {}", operationLog);
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("日志保存失败，消息即将重回队列: {}", e.getMessage(), e);

            try {
                //消费失败，消息重回队列
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                log.error("消息重回队列失败: {}", ex.getMessage(), ex);
            }
        }
    }
}//使用ai对代码进行了修改，来进行手动ack




