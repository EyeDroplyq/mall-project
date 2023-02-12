package com.home.mall.gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


/**
 * @description: 进行跨域配置
 * @author: lyq
 * @createDate: 11/1/2023
 * @version: 1.0
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedMethod("*");//允许所有方法进行跨域
        corsConfiguration.addAllowedOrigin("*");//允许所有来源进行跨域
        corsConfiguration.addAllowedHeader("*");//允许所有请求头进行跨域
        corsConfiguration.setAllowCredentials(true);//允许携带cookie进行跨域
        source.registerCorsConfiguration("/**", corsConfiguration);
        CorsWebFilter corsWebFilter = new CorsWebFilter(source);
        return corsWebFilter;
    }
}
