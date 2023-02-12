package com.home.mall.search.service;

import com.home.mall.search.vo.SearchParamVo;
import com.home.mall.search.vo.SearchResult;

/**
 * @description:
 * @author: lyq
 * @createDate: 29/1/2023
 * @version: 1.0
 */
public interface MallSearchService {
    SearchResult search(SearchParamVo searchParamVo);
}
