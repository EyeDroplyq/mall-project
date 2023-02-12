package com.home.common.constant;

/**
 * @description: 采购需求状态
 * @author: lyq
 * @createDate: 17/1/2023
 * @version: 1.0
 */
public class PurchaseDetailStatus {
    public enum PurchaseDetailEnum{
        CREATED(0,"新建"),ASSIGN(1,"已分配")
        ,BUYING(2,"正在采购"),FINISHED(3,"已完成")
        ,HASERROR(4,"采购失败");


        private Integer code;
        private String msg;

        PurchaseDetailEnum(Integer code, String msg) {
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
