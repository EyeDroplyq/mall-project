package com.home.mall.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: lyq
 * @createDate: 6/2/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginVo {
    private String loginacct;
    private String password;
}
