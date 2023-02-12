package io.renren.modules.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.app.dao.NewsDao;
import io.renren.modules.app.dao.VideoDao;
import io.renren.modules.app.entity.NewsEntity;
import io.renren.modules.app.entity.ThumbEntity;
import io.renren.modules.app.entity.VideoEntity;
import io.renren.modules.app.service.NewsService;
import io.renren.modules.app.service.ThumbService;
import io.renren.modules.app.service.VideoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/6/2022
 * @version: 1.0
 */
@Service
@Log4j2
public class NewsServiceImpl extends ServiceImpl<NewsDao, NewsEntity> implements NewsService {
    @Resource
    private ThumbService thumbService;

    @Override
    public List<NewsEntity> getNewsList() {
        List<NewsEntity> newsEntities = baseMapper.selectList(null);
        return newsEntities;
    }
}
