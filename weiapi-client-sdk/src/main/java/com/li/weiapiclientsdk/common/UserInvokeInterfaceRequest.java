package com.li.weiapiclientsdk.common;

import lombok.Data;

@Data
public class UserInvokeInterfaceRequest {
	/**
	 * 接口编号
	 */
	private Long id;
	/**
	 * 接口名
	 */
	private String interfaceName;
	/**
	 * 请求地址
	 */
	private String url;
	/**
	 * 请求方法
	 */
	private String method;
	/**
	 * 用户传来的参数
	 */
	private String userParams;
}
