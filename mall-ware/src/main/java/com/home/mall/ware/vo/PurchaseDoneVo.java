package com.home.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @description: 完成采购请求的实体
 * @author: lyq
 * @createDate: 17/1/2023
 * @version: 1.0
 */
@Data
public class PurchaseDoneVo {

    private Long id; //采购单id

    List<Item> items;

}
