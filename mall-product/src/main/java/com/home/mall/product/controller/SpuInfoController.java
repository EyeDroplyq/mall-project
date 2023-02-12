package com.home.mall.product.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.home.common.exception.BizErrorEnum;
import com.home.mall.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.home.mall.product.entity.SpuInfoEntity;
import com.home.mall.product.service.SpuInfoService;
import com.home.common.utils.PageUtils;
import com.home.common.utils.R;



/**
 * spu信息
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 商品上架
     */
    @PostMapping("/{spuId}/up")
    public R spuUp(@PathVariable("spuId")Long spuId){
        try {
            spuInfoService.up(spuId);
            return R.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return R.error(BizErrorEnum.PRODUCTUP_EXCEPTION.getCode(),BizErrorEnum.PRODUCTUP_EXCEPTION.getMsg());
        }

    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.querySpuInfo(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuSaveVo vo){
		spuInfoService.saveSpuInfo(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
