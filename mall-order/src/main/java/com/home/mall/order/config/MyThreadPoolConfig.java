package com.home.mall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description: 全局线程池配置
 * @author: lyq
 * @createDate: 1/2/2023
 * @version: 1.0
 */
@Configuration
public class MyThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfig threadPoolConfig){
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(threadPoolConfig.getCoreSize(),
                threadPoolConfig.getMaxSize(),
                threadPoolConfig.getKeepAliveTime(),
                    TimeUnit.SECONDS,new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        return poolExecutor;
    }
}
