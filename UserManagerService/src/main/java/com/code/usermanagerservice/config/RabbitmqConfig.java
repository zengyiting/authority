package com.code.usermanagerservice.config;

import com.code.usermanagerservice.enums.ConfigEnum;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
    // 定义交换机（direct 类型）
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(ConfigEnum.EXCHANGE_NAME.getValue());
    }

    // 定义队列
    @Bean
    public Queue queue() {
        return new Queue(ConfigEnum.QUEUE_NAME.getValue(), true);  // true表示持久化队列
    }

    // 绑定队列到交换机，指定路由键
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(directExchange()).with(ConfigEnum.ROUTING_KEY.getValue());
    }
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}//by ai
