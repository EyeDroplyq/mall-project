package com.home.mall.ware.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/2/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemVo {
    private Long skuId;//商品id
    private String title;//商品标题
    private String image;//图片地址
    private List<String> skuAttr;//属性组合信息,哪套配置
    private BigDecimal price;//价格
    private Integer count;//数量
    private BigDecimal totalPrice;//总价

    private BigDecimal weight;
}
