package com.home.mall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: lyq
 * @createDate: 1/2/2023
 * @version: 1.0
 */

@Component
@ConfigurationProperties(prefix = "mall.thread")
@Data
public class ThreadPoolConfig {

    private Integer coreSize;

    private Integer maxSize;

    private Integer keepAliveTime;

}
