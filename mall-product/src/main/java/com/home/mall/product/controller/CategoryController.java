package com.home.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.home.mall.product.entity.CategoryEntity;
import com.home.mall.product.service.CategoryService;
import com.home.common.utils.PageUtils;
import com.home.common.utils.R;



/**
 * 商品三级分类
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有的分类列表，以及每个分类的子分类，并用树形结构封装起来
     */
    @RequestMapping("/list/tree")
    public R list(){
        List<CategoryEntity> categoryEntityList=categoryService.listWithTree();
        return R.ok().put("data", categoryEntityList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 批量修改
     */
    @RequestMapping("/update/sort")
    public R updateSorts(@RequestBody CategoryEntity[] category){
        //批量更新,主要是更新排序
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }


    /**
     * 修改 修改操作不能简单的更新该表，也需要更新与该表关联的表
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateCascade(category);

        return R.ok();
    }

    /**
     *  如果方法中有了@RequestBody说明这是一个post请求
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds){
		categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
