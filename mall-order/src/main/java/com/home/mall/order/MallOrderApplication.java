package com.home.mall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
//其中exposeProxy = true的设置是暴露代理
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.home.mall.order.dao")
@EnableRabbit
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
@EnableRedisHttpSession
public class MallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallOrderApplication.class, args);
    }

}
