package com.home.mall.ware.listener;

import com.home.common.to.OrderTo;
import com.home.mall.ware.service.WareOrderTaskDetailService;
import com.home.mall.ware.service.WareSkuService;
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
@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class HandlerOrderListener {
    @Autowired
    private WareSkuService wareSkuService;
    @RabbitHandler
    public void unlockStock(OrderTo orderTo, Message message, Channel channel) throws IOException {
        try {
            wareSkuService.unlockStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
