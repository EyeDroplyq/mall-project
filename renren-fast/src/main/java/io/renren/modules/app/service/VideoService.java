package io.renren.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.app.entity.VideoEntity;

import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/6/2022
 * @version: 1.0
 */
public interface VideoService extends IService<VideoEntity> {
    List<VideoEntity> getVideoList();
}
