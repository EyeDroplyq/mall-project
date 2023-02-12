package io.renren.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: lyq
 * @createDate: 20/6/2022
 * @version: 1.0
 */
@Data
@TableName("news_thumb")
public class ThumbEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Integer thumbId;
    @TableField(value = "thumb_url")
    private String thumbUrl;
    @TableField(value = "news_id")
    private String newsId;

}
