package com.home.mall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.home.mall.product.entity.AttrEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 返回分类下的所有组以及组的属性的实体
 * @author: lyq
 * @createDate: 16/1/2023
 * @version: 1.0
 */
@Data
public class AttrAndAttrResGroupVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;


}
