package com.home.mall.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/2/2023
 * @version: 1.0
 */
@Controller
public class testOrder {

    @GetMapping("{page}.html")
    public String testPage(@PathVariable("page")String page){
        return page;
    }
}
