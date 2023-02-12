package com.home.mall.member.exception;

/**
 * @description:
 * @author: lyq
 * @createDate: 6/2/2023
 * @version: 1.0
 */
public class PhoneExistException extends RuntimeException{
    public PhoneExistException() {
        super("手机号已存在");
    }
}
