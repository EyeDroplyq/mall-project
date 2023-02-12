package com.home.mall.member.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: lyq
 * @createDate: 7/2/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserVo {
    private String access_token;
    private String remind_in;
    private Long expires_in;
    private String uid;
    private String isRealName;

}
