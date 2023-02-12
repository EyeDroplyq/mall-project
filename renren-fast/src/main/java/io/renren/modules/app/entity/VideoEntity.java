package io.renren.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/6/2022
 * @version: 1.0
 */
@Data
@TableName("video_list")
public class VideoEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Integer vid;
    @TableField(value = "vtitle")
    private String vtitle;
    @TableField(value = "author")
    private String author;
    @TableField(value = "coverUrl")
    private String coverUrl;
    @TableField(value = "headurl")
    private String headurl;
    @TableField(value = "comment_num")
    private Integer commentNum;
    @TableField(value = "like_num")
    private Integer likeNum;
    @TableField(value = "collect_num")
    private Integer collectNum;
    @TableField(value = "create_time")
    private Date createTime;
    @TableField(value = "update_time")
    private Date updateTime;
    @TableField(value = "category_id")
    private Integer categoryId;


}
