package com.home.mall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author: lyq
 * @createDate: 21/2/2023
 * @version: 1.0
 */
@Configuration
public class MallRequestInterceptor {
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //spring中有一个方便我们获取线程上下文中的内容的
                //RequestContextHolder底层是ThreadLocal
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(requestAttributes!=null){
                    //获取老请求的请求头中的Cookie信息
                    HttpServletRequest request = requestAttributes.getRequest();
                    if (request!=null){
                        String cookie = request.getHeader("Cookie");
                        //将老请求中的Cookie设置到新创建的请求中
                        template.header("Cookie",cookie);
                    }

                }
            }
        };
    }
}
