package com.home.mall.order.config;

import com.home.mall.order.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description:
 * @author: lyq
 * @createDate: 22/2/2023
 * @version: 1.0
 */
@Configuration
public class WebInterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor interceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**");
    }
}
