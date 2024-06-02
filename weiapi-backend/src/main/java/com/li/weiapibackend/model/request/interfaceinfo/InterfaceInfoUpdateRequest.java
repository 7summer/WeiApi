package com.li.weiapibackend.model.request.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新接口类
 */
@Data
public class InterfaceInfoUpdateRequest implements Serializable {
	/**
	 * 接口编号
	 */
	private Long id;

	/**
	 * 接口名
	 */
	private String interfaceName;

	/**
	 * 接口描述
	 */
	private String description;

	/**
	 * 接口地址
	 */
	private String url;

	/**
	 * 请求头
	 */
	private String requestHeader;

	/**
	 * 响应头
	 */
	private String responseHeader;

	/**
	 * 请求类型
	 */
	private String method;

	/**
	 * 请求参数
	 */
	private String params;

	/**
	 * 接口状态
	 */
	private Integer interfaceStatus;
}
