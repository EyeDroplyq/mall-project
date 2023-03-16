package com.home.mall.ware.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @description:
 * @author: lyq
 * @createDate: 11/2/2023
 * @version: 1.0
 */
@Configuration
public class RabbitMqConfig {

    //因为confirmCallback在rabbitTemplate中，所以我们注入这个模板
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter() {
        //指定使用json序列化对象传输消息
        return new Jackson2JsonMessageConverter();
    }

    //初始化RabbitMqConfig类之后调用这个方法
    @PostConstruct
    public void initRabbit() {
        //如果发送端成功将消息发送到broker代理的话，会执行这个回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String message) {
                System.out.println("correlationData[" + correlationData + "]" + "---->ack[" + ack + "]" + "----->message[" + message + "]");
            }
        });

        //当交换机的消息没有成功发送给队列的时候会执行这个回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("message[" + message + "]" + "---->replyCode[" + replyCode + "]" + "----->replyText[" + replyText + "]" + "----->exchange[" + exchange + "]" + "----->routingKey[" + routingKey + "]");
            }
        });
    }
}
