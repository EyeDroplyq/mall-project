package com.home.mall.member.exception;

/**
 * @description:
 * @author: lyq
 * @createDate: 6/2/2023
 * @version: 1.0
 */
public class UserNameExistException extends RuntimeException{
    public UserNameExistException() {
        super("用户名已存在");
    }
}
