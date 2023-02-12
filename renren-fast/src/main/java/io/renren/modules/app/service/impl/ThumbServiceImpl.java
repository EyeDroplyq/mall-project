package io.renren.modules.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.app.dao.NewsDao;
import io.renren.modules.app.dao.ThumbDao;
import io.renren.modules.app.entity.NewsEntity;
import io.renren.modules.app.entity.ThumbEntity;
import io.renren.modules.app.service.NewsService;
import io.renren.modules.app.service.ThumbService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/6/2022
 * @version: 1.0
 */
@Service
@Log4j2
public class ThumbServiceImpl extends ServiceImpl<ThumbDao, ThumbEntity> implements ThumbService {

}
