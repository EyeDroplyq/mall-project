package com.home.mall.order.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: lyq
 * @createDate: 11/2/2023
 * @version: 1.0
 */
@Configuration
public class RabbitMqConfig {
    @Bean
    public MessageConverter messageConverter(){
        //指定使用json序列化对象传输消息
        return new Jackson2JsonMessageConverter();
    }
}
