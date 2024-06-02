package com.li.weiapibackend.model.request.interfaceinfo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.li.weiapibackend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {
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
	 * 请求类型
	 */
	private String method;

	/**
	 * 接口状态
	 */
	private Integer interfaceStatus;
}
