package com.home.mall.search.service;

import com.home.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 24/1/2023
 * @version: 1.0
 */
public interface SkuSaveService {
    void skuSave(List<SkuEsModel> esModels) throws IOException;
}
