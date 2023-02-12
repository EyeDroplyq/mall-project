package com.home.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 17/1/2023
 * @version: 1.0
 */
@Data
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
