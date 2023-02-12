package com.home.common.constant;

/**
 * @description: 商品服务的常量
 * @author: lyq
 * @createDate: 15/1/2023
 * @version: 1.0
 */
public class ProductConstant {
    public enum AttrEnum{
        ATTR_BASE_TYPE(1,"基本属性"),ATTR_SALE_TYPE(0,"销售属性");
        private Integer code;
        private String msg;

        AttrEnum(Integer code, String msg) {
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

    public enum StatusEnum{
        NEW(0,"新建"),UP(1,"上架"),DOWN(2,"下架");
        private Integer code;
        private String msg;

        StatusEnum(Integer code, String msg) {
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
