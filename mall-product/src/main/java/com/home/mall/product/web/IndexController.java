package com.home.mall.product.web;

import com.home.mall.product.entity.CategoryEntity;
import com.home.mall.product.service.CategoryService;
import com.home.mall.product.vo.CataLogLevel2Vo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @description: 首页以及跳转的controller
 * @author: lyq
 * @createDate: 25/1/2023
 * @version: 1.0
 */
@Controller
public class IndexController {
    @Resource
    private CategoryService categoryService;
    @GetMapping({"/","/index.html"})
    public String index(Model model){
        List<CategoryEntity> categoryEntities = categoryService.getFirstCategory(); //查询第一级分类
         model.addAttribute("categorys", categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping(value = "/index/catalog.json")
    public Map<String, List<CataLogLevel2Vo>> cataLogJson(){
        Map<String, List<CataLogLevel2Vo>> data=categoryService.cataLogJson();
        return data;
    }

}
