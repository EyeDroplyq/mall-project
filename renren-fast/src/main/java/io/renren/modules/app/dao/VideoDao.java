package io.renren.modules.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.app.entity.VideoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/6/2022
 * @version: 1.0
 */
@Mapper
public interface VideoDao extends BaseMapper<VideoEntity> {
}
