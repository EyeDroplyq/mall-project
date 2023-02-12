package com.home.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 这个枚举类中封装整个项目的所有异常的错误码和错误信息
 * @author: lyq
 * @createDate: 13/1/2023
 * @version: 1.0
 */

public enum BizErrorEnum {
    VALID_EXCEPTION(10001,"数据校验错误"),
    SMS_CODE_EXCEPTION(10002,"获取验证码频率太高,请稍后再试"),
    UNKNOWN_EXCEPTION(10000,"系统未知错误"),
    USERNAME_EXIST_EXCEPTION(15000,"用户名已存在"),
    PHONE_EXIST_EXCEPTION(15001,"手机号已存在"),
    USER_LOGIN_EXCEPTION(15002,"用户名或密码错误"),
    PRODUCTUP_EXCEPTION(11000,"商品上架错误");

    private Integer code;
    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    BizErrorEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
