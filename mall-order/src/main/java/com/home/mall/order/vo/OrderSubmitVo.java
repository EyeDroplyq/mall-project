package com.home.mall.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * @description: 订单确认页点击提交订单的时候给我们发过来的请求体
 * @author: lyq
 * @createDate: 22/2/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSubmitVo {
    private final Long addrId=1L;//收件人的地址
    private Integer payType;//支付类型
    private String orderToken;//防重复提交的token令牌
    private BigDecimal payPrice;//应付价格
    private String note;//订单备注

    //不用传订单项，因为我们在生成订单的时候回去重新查一遍选中的购物车
    //也不用传用户信息，因为用户信息都放在session中了
}
