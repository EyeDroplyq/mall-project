package com.home.mall.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.home.common.to.StockTo;
import com.home.common.to.WareOrderDetailTo;
import com.home.common.utils.R;
import com.home.mall.ware.constant.OrderStatusEnum;
import com.home.mall.ware.entity.WareOrderTaskDetailEntity;
import com.home.mall.ware.entity.WareOrderTaskEntity;
import com.home.mall.ware.service.WareSkuService;
import com.home.mall.ware.vo.OrderVo;
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
 * @createDate: 14/3/2023
 * @version: 1.0
 */
@Service
//监听rabbitmq的队列进行库存的解锁
@RabbitListener(queues = "stock.release.stock.queue")
public class RabbitmqListener {
    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    public void unlockStock(StockTo stockTo,Message message, Channel channel) throws IOException {
        try {
            System.out.println("消费队列中的消息");
            wareSkuService.unlockStock(stockTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            //如果有异常就说明需要重新消费消息
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
