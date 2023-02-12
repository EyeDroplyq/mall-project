package com.home.mall.cart.to;

import lombok.Data;

/**
 * @description:
 * @author: lyq
 * @createDate: 8/2/2023
 * @version: 1.0
 */
@Data
public class UserInfoTo {
    private Long userId;//用户id
    private String userKey;//临时用户的key
    private Boolean tempUser=false;//是否是临时用户
}
