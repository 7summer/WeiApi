package com.li.weiapicommon.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class InnerIdentify implements Serializable {
	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 密钥
	 */
	private String accessKey;

	/**
	 * 密钥
	 */
	private String secretKey;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 是否删除（逻辑）
	 */
	private Integer isDelete;
}
