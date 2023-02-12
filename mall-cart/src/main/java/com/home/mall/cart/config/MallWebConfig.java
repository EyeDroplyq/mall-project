package com.home.mall.cart.config;

import com.home.mall.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description:
 * @author: lyq
 * @createDate: 8/2/2023
 * @version: 1.0
 */
@Configuration
public class MallWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //将自己写的拦截器配置进来
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
