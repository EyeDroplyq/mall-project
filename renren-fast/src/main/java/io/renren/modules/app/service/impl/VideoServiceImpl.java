package io.renren.modules.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.app.dao.VideoDao;
import io.renren.modules.app.entity.VideoEntity;
import io.renren.modules.app.service.VideoService;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/6/2022
 * @version: 1.0
 */
@Service
@Log4j2
public class VideoServiceImpl extends ServiceImpl<VideoDao, VideoEntity> implements VideoService {

    @Override
    public List<VideoEntity> getVideoList() {
        log.info("查询所有的视频数据");
        List<VideoEntity> videoEntityList = baseMapper.selectList(null);
        return videoEntityList;
    }
}
