package com.home.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.home.common.valid.AddGroup;
import com.home.common.valid.ListValue;
import com.home.common.valid.UpdateGroup;
import com.home.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author lyq
 * @email 1525761478@qq.com
 * @date 2023-01-09 19:35:24
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改操作必须有id",groups = {UpdateGroup.class,UpdateStatusGroup.class})
	@Null(message = "添加操作时必须不能携带id",groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "名称不能为空",groups = {AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "logo不能为空",groups = {AddGroup.class})
	@URL(message = "logo必须是url地址",groups = {AddGroup.class,UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(message = "显示状态不能为空",groups = {AddGroup.class})
	//自定义的校验注解，要求showStatus字段必须是0或者1
	@ListValue(vals={0,1},groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	//@pattern注解是自定义规则
	@Pattern(regexp="^[a-zA-Z]$",message = "检索首字母只能是a-z或者A-Z之间的字母",groups = {AddGroup.class,UpdateGroup.class})
	@NotBlank(message = "检索首字母不能为空",groups = {AddGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序字段不能为空",groups = {AddGroup.class})
	@Min(value = 0,message = "排序字段必须大于等于0",groups = {AddGroup.class,UpdateGroup.class})
	private Integer sort;

}
