package com.home.mall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @description:
 * @author: lyq
 * @createDate: 27/1/2023
 * @version: 1.0
 */
@Configuration
public class RedissonConfig {
    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.237.138:6379");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
