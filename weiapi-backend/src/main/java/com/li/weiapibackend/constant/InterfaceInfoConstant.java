package com.li.weiapibackend.constant;

import java.util.ArrayList;
import java.util.List;

public interface InterfaceInfoConstant {
	int pageIndex = 1;
	int pageSize = 4;
	int maxPageSize = 4;

	/**
	 * 请求方法列表
	 */
	List<String> methodList = new ArrayList<String>(){{
		add("GET");
		add("POST");
		add("HEAD");
		add("PUT");
		add("DELETE");
		add("CONNECT");
		add("OPTIONS");
		add("TRACE");
	}};

	/**
	 * 接口状态列表
	 */
	List<Integer> interfaceInfoStatusList = new ArrayList<Integer>(){{
		// 上线
		add(0);
		// 下线
		add(1);
	}};

	/**
	 * 接口上线
	 */
	int ONLINE = 0;
	/**
	 * 接口下线
	 */
	int OFFLINE = 1;
}
