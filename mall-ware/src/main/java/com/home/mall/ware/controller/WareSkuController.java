package com.home.mall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.home.common.to.HasStockTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.home.mall.ware.entity.WareSkuEntity;
import com.home.mall.ware.service.WareSkuService;
import com.home.common.utils.PageUtils;
import com.home.common.utils.R;



/**
 * 商品库存
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 23:15:16
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 查询是否有库存
     */
    @PostMapping("/hasstock")
    public R hasStock(@RequestBody List<Long> skuIds){
        List<HasStockTo> data=wareSkuService.hasStock(skuIds);
        return R.ok().put("data", data);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
