package com.home.mall.order.listener;

import com.home.mall.order.entity.OrderEntity;
import com.home.mall.order.service.OrderService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @description:
 * @author: lyq
 * @createDate: 15/3/2023
 * @version: 1.0
 */
@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderListener {
    @Autowired
    private OrderService orderService;
    //到期后将订单释放
    @RabbitHandler
    public void releaseOrder(OrderEntity order, Message message, Channel channel) throws IOException {
        try{
            orderService.releaseOrder(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
