package com.home.mall.member.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @description:
 * @author: lyq
 * @createDate: 6/2/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRegistVo {
    @NotEmpty(message = "用户名不能为空")
    @Length(min=3,max=16,message = "用户名长度在3-16位之间")
    private String userName;
    @NotEmpty(message = "密码不能为空")
    @Length(min=3,max=16,message = "密码长度在3-16位之间")
    private String password;
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式不正确")
    private String phone;
    @NotEmpty(message = "验证码不能为空")
    @Length(min=5,max=5,message = "验证码长度不是5位")
    private String code;
}
