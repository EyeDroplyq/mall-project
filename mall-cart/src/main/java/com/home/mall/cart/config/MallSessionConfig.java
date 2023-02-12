package com.home.mall.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @description:
 * @author: lyq
 * @createDate: 7/2/2023
 * @version: 1.0
 */
@Configuration
public class MallSessionConfig {
    //自定义cookie配置，主要是配置cookie的作用域，放大作用域
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("MALLSESSION");
        serializer.setDomainName("mall.com");
        return serializer;
    }

    //redis序列化配置
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

}
