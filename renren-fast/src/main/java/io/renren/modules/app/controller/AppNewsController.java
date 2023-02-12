package io.renren.modules.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.utils.R;
import io.renren.modules.app.entity.NewsEntity;
import io.renren.modules.app.service.NewsService;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/6/2022
 * @version: 1.0
 */
@RestController
@Log4j2
@Api(value = "资讯接口")
@RequestMapping("/app")
public class AppNewsController {
    @Autowired
    private NewsService newsService;
    @GetMapping("/news/api/list")
    public R getNewsList(){
        List<NewsEntity> newsEntities= newsService.getNewsList();
        QueryWrapper<NewsEntity> queryWrapper = new QueryWrapper<>();
        return R.ok().put("list",newsEntities);
    }

}
