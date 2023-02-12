package com.home.mall.member.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: lyq
 * @createDate: 6/2/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginVo {
    private String loginacct;
    private String password;
}
