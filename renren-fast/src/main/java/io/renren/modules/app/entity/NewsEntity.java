package io.renren.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/6/2022
 * @version: 1.0
 */
@Data
@TableName("news")
public class NewsEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Integer newsId;
    @TableField(value = "news_title")
    private String newsTitle;
    @TableField(value = "author_name")
    private String authorName;
    @TableField(value = "header_url")
    private String headerurl;
    @TableField(value = "comment_count")
    private Integer commentCount;
    @TableField(value = "release_date")
    private Date releaseDate;
    @TableField(value = "type")
    private Integer type;
//    @TableField(exist=false)
//    private List<ThumbEntity> thumbEntities=new ArrayList<>();


}
