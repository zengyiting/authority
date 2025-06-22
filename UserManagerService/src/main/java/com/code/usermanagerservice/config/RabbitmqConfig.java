package com.code.usermanagerservice.config;

import com.alibaba.fastjson.JSON;
import com.code.usermanagerservice.enums.ConfigEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class RabbitmqConfig {



    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(ConfigEnum.EXCHANGE_NAME.getValue());
    }

    @Bean
    public Queue queue() {
        return new Queue(ConfigEnum.QUEUE_NAME.getValue(), true);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(directExchange()).with(ConfigEnum.ROUTING_KEY.getValue());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
