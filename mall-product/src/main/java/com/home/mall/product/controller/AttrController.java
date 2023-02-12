package com.home.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.home.mall.product.entity.ProductAttrValueEntity;
import com.home.mall.product.vo.AttrResponseVo;
import com.home.mall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.home.mall.product.entity.AttrEntity;
import com.home.mall.product.service.AttrService;
import com.home.common.utils.PageUtils;
import com.home.common.utils.R;



/**
 * 商品属性
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 根据分类的id查询对应的属性
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    public R listAttr(@RequestParam Map<String, Object> params,@PathVariable("catelogId")Long catelogId,@PathVariable("attrType") String type){
        PageUtils page = attrService.queryAttr(params,catelogId,type);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
		AttrResponseVo attr = attrService.getAttr(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 修改spu规格参数
     */
    @RequestMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable("spuId") Long spuId,@RequestBody List<ProductAttrValueEntity> entities){
        attrService.updateSpuAttrs(spuId,entities);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

    /**
     * 获取spu规格用来进行回显
     */
    @RequestMapping("/base/listforspu/{spuId}")
    public R getSpuAttr(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> data=attrService.getSpuAttrs(spuId);
        return R.ok().put("data", data);
    }

}
