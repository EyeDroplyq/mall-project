package com.home.mall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.home.mall.ware.vo.MergeVo;
import com.home.mall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.home.mall.ware.entity.PurchaseEntity;
import com.home.mall.ware.service.PurchaseService;
import com.home.common.utils.PageUtils;
import com.home.common.utils.R;



/**
 * 采购信息
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 23:15:16
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 查询没有分配的采购单
     */
    @GetMapping("/unreceive/list")
    public R listUnreceive(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryUnreceive(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping(value = "/merge")
    public R merge(@RequestBody MergeVo mergeVo){
        purchaseService.mergePurchase(mergeVo);
        return R.ok();
    }

    @PostMapping(value = "/received")
    public R receive(@RequestBody List<Long> purchaseIds){
        purchaseService.receivePurchase(purchaseIds);
        return R.ok();
    }

    @PostMapping(value = "/done")
    public R done(@RequestBody PurchaseDoneVo purchaseDoneVo){
        purchaseService.purchaseDone(purchaseDoneVo);
        return R.ok();
    }

}
