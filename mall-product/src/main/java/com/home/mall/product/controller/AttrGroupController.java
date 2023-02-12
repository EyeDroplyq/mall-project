package com.home.mall.product.controller;

import java.util.*;
import java.util.stream.Collectors;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.home.mall.product.entity.AttrEntity;
import com.home.mall.product.service.AttrService;
import com.home.mall.product.service.CategoryService;
import com.home.mall.product.vo.AttrAndAttrResGroupVo;
import com.home.mall.product.vo.AttrAttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.home.mall.product.entity.AttrGroupEntity;
import com.home.mall.product.service.AttrGroupService;
import com.home.common.utils.PageUtils;
import com.home.common.utils.R;

import javax.annotation.Resource;


/**
 * 属性分组
 *
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private AttrService attrService;

    /**
     * 列表  根据三级分类id来查询对应的属性分组
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable Long catelogId){
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
            Long[] path=categoryService.findCatelogPath(catelogId);
            attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }


    @GetMapping(value = "/{attrgroupId}/attr/relation")
    public R getAttrAttrGroupRelation(@PathVariable("attrgroupId")Long attrGroupId){
        List<AttrEntity> data = attrGroupService.getAllAttr(attrGroupId);
        return R.ok().put("data",data);
    }

    @PostMapping(value = "/attr/relation")
    public R saveAttrAttrGroupRelation(@RequestBody AttrAttrGroupRelationVo[] relationVos){
        attrGroupService.saveAttrAttrGroupRelation(relationVos);
        return R.ok();
    }

    /**
     * 批量删除属性和属性组的关系
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrAttrGroupRelationVo[] relationVo){
        attrGroupService.removeAttrAttrGroupRelation(relationVo);
        return R.ok();
    }

    /**
     * 获取当前属性所属组中没有关联的其他属性信息
     * @param attrgroupId
     * @return
     */
    @GetMapping(value = "/{attrgroupId}/noattr/relation")
    public R getNoAttrRelation(@RequestParam Map<String, Object> params,@PathVariable("attrgroupId") Long attrgroupId){
        PageUtils page = attrService.getNoRelationAttr(params,attrgroupId);
        return R.ok().put("page", page);
    }

    @GetMapping(value = "/{catelogId}/withattr")
    public R getWithAttr(@PathVariable("catelogId") Long catelogId){
        List<AttrAndAttrResGroupVo> data=attrGroupService.getAttrAndAttrGroupByCatId(catelogId);
        return R.ok().put("data",data);

    }
}
