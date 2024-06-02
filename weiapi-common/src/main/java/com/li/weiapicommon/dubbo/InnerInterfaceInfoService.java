package com.li.weiapicommon.dubbo;

import com.li.weiapicommon.model.InnerInterfaceInfo;

public interface InnerInterfaceInfoService {
	/**
	 * 通过路径和方法查询接口信息
	 * @param url
	 * @param method
	 * @return
	 */
	InnerInterfaceInfo getInterfaceInfoByPathAndMethod(String url, String method);
}
