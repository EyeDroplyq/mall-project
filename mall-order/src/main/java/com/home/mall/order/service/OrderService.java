package com.home.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.home.common.utils.PageUtils;
import com.home.mall.order.entity.OrderEntity;
import com.home.mall.order.vo.*;

import java.util.Map;

/**
 * 订单
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 22:48:14
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //封装确认订单页要显示的vo
    OrderConfirmVo orderConfirm();

    // 创建订单、验证token令牌、验价、锁库存
    OrderSubmitRespVo submitOrder(OrderSubmitVo vo);

    OrderEntity getOrderStaus(String orderSn);

    void releaseOrder(OrderEntity order);

    PayVo getPayInfoByOrderSn(String orderSn);

    PageUtils queryPageList(Map<String, Object> params);

    String handlerOrder(PayAsyncVo vo);
}

