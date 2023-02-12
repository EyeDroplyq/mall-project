package io.renren.modules.app.controller;

import io.renren.common.utils.R;
import io.renren.modules.app.entity.VideoEntity;
import io.renren.modules.app.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/6/2022
 * @version: 1.0
 */
@RestController
@RequestMapping("/app")
@Api("APP视频接口")
public class AppVideoController {
    @Autowired
    private VideoService videoService;
    @ApiOperation("查询视频")
    @GetMapping(value = "/videolist/listAll")
    public R videoList(){
        List<VideoEntity> videoEntityList=videoService.getVideoList();
        return R.ok().put("list",videoEntityList);
    }
}
