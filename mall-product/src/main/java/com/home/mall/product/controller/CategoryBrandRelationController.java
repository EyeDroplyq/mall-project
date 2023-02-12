package com.home.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.home.mall.product.entity.BrandEntity;
import com.home.mall.product.vo.BrandResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.home.mall.product.entity.CategoryBrandRelationEntity;
import com.home.mall.product.service.CategoryBrandRelationService;
import com.home.common.utils.PageUtils;
import com.home.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 查询品牌关联的分类
     */
    @GetMapping("/catelog/list")
    public R list(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
        return R.ok().put("data", data);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 得到分类关联的品牌
     */
    @GetMapping("/brands/list")
    public R getBrand(@RequestParam(value = "catId",required = true) Long catId){
        List<BrandEntity> list=categoryBrandRelationService.getBrand(catId);
        //封装成我们需要的返回实体vo
        List<BrandResponseVo> data = list.stream().map((item) -> {
            BrandResponseVo brandResponseVo = new BrandResponseVo();
            brandResponseVo.setBrandId(item.getBrandId());
            brandResponseVo.setBrandName(item.getName());
            return brandResponseVo;
        }).collect(Collectors.toList());
        return R.ok().put("data", data);
    }

}
