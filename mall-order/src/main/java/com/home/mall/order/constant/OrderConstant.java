package com.home.mall.order.constant;

/**
 * @description:
 * @author: lyq
 * @createDate: 22/2/2023
 * @version: 1.0
 */
public class OrderConstant {
    //保证幂等性的token令牌的key格式是：order:token:{userId}
    public static final  String USER_ORDER_PREFIX_TOKEN="order:token:";

}
