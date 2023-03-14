package com.home.mall.order;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallOrderApplicationTests {

    @Autowired
    private AmqpAdmin admin;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessageTest(){
        rabbitTemplate.convertAndSend("exchange.mall","mall.queue","hello rabbitmq");
        //序列化操作时会把系统当前类的serialVersionUID写入到序列化文件中，当反序列化时系统会自动检测文件中的serialVersionUID，判断它是否与当前类中的serialVersionUID一致。如果一致说明序列化文件的版本与当前类的版本是一样的，可以反序列化成功，否则就失败；
    }

    @Test
    public void amqpAdmin() {
        admin.declareExchange(new DirectExchange("exchange.mall"));
        admin.declareQueue(new Queue("mall.queue"));
        admin.declareBinding(new Binding("mall.queue", Binding.DestinationType.QUEUE,"exchange.mall","mall.queue",null));

    }
    @Test
    public void test(){
        String s1=new String("abc");
        String s2="abc";
        String s3="a"+"bc";
        System.out.println(s1==s2);
        System.out.println(s2==s3);
        System.out.println(s1.compareTo(s3));
    }
    @Test
    public void test02(){

    }

}
