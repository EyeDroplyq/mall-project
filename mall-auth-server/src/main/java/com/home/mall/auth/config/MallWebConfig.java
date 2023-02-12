package com.home.mall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description:
 * @author: lyq
 * @createDate: 6/2/2023
 * @version: 1.0
 */
    @Configuration
    public class MallWebConfig implements WebMvcConfigurer {
        //配置视图映射功能
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/login.html").setViewName("login");
            registry.addViewController("/reg.html").setViewName("reg");
        }
    }
