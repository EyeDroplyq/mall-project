package com.home.mall.search.controller;

import com.home.mall.search.service.MallSearchService;
import com.home.mall.search.vo.SearchParamVo;
import com.home.mall.search.vo.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: lyq
 * @createDate: 29/1/2023
 * @version: 1.0
 */
@Controller
public class SearchController {
    @Resource
    private MallSearchService mallSearchService;

    @GetMapping(value = "/list.html")

    public String listPage(SearchParamVo searchParamVo, Model model, HttpServletRequest request){
        searchParamVo.setQueryString(request.getQueryString());
        SearchResult result=mallSearchService.search(searchParamVo);
        model.addAttribute("result",result);
        return "list";
    }
}
