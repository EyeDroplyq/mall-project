package com.home.mall.search.controller;

import com.home.common.exception.BizErrorEnum;
import com.home.common.to.es.SkuEsModel;
import com.home.common.utils.R;
import com.home.mall.search.service.SkuSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 24/1/2023
 * @version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/search/save")
public class SkuSaveController {

    @Resource
    private SkuSaveService skuSaveService;
    @PostMapping("/product")
    public R skuSave(@RequestBody List<SkuEsModel> esModels)  {
        try {
            skuSaveService.skuSave(esModels);
            return R.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return R.error(BizErrorEnum.PRODUCTUP_EXCEPTION.getCode(),BizErrorEnum.PRODUCTUP_EXCEPTION.getMsg());
        }
    }

}
