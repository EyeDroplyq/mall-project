package com.home.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.home.common.utils.PageUtils;
import com.home.mall.ware.entity.PurchaseEntity;
import com.home.mall.ware.vo.MergeVo;
import com.home.mall.ware.vo.PurchaseDoneVo;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 23:15:16
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryUnreceive(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void receivePurchase(List<Long> purchaseIds);

    void purchaseDone(PurchaseDoneVo purchaseDoneVo);
}

