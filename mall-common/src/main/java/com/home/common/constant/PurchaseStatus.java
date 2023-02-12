package com.home.common.constant;

/**
 * @description: 采购单状态
 * @author: lyq
 * @createDate: 17/1/2023
 * @version: 1.0
 */

public class PurchaseStatus {
    public enum PurchaseEnum{
        CREATED(0,"新建"),ASSIGN(1,"已分配")
        ,RECEIVED(2,"已领取"),FINISHED(3,"已完成")
        ,HASERROR(4,"有异常");


        private Integer code;
        private String msg;

        PurchaseEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
