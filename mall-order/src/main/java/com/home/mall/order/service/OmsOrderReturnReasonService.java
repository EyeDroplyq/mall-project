package com.home.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.home.common.utils.PageUtils;
import com.home.mall.order.entity.OmsOrderReturnReasonEntity;

import java.util.Map;

/**
 * 退货原因
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 22:43:14
 */
public interface OmsOrderReturnReasonService extends IService<OmsOrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

