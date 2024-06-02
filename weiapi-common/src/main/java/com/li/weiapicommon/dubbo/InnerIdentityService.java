package com.li.weiapicommon.dubbo;

import com.li.weiapicommon.model.InnerIdentify;

public interface InnerIdentityService {
	/**
	 * 通过用户编号得到密钥
	 * @param accessKey 密钥
	 * @return
	 */
	InnerIdentify getIdentityByUserId(String accessKey);
}
