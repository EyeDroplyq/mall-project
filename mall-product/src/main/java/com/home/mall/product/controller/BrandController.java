package com.home.mall.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.home.common.valid.AddGroup;
import com.home.common.valid.UpdateGroup;
import com.home.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.home.mall.product.entity.BrandEntity;
import com.home.mall.product.service.BrandService;
import com.home.common.utils.PageUtils;
import com.home.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand/*, BindingResult result*/){
//        Map<String,String> errorMap=new HashMap<>();
//        if (result.hasErrors()){ //如果出现了错误
//            //封装我们要返回的错误信息
//            List<FieldError> errors = result.getFieldErrors();
//            for (FieldError item : errors) {
//                String message = item.getDefaultMessage();
//                String field = item.getField();
//                errorMap.put(field,message);
//            }
//        }else{
//
//        }
        //如果没有错误就进行保存
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改 这里的修改不能简单的更新自己的表，因为有其他的表引用了该表中的一些字段，所以该表改变后关联表也需要进行改变
     */
    @RequestMapping("/update")
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand){
		brandService.updateBDetail(brand);

        return R.ok();
    }

    /**
     * 因为前端中有一个通过按钮直接修改状态的功能，所以我们创建一个修改状态的方法
     */
    @RequestMapping("/update/status")
    public R updateStatus(@Validated(UpdateStatusGroup.class) @RequestBody BrandEntity brand){
        brandService.updateById(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
