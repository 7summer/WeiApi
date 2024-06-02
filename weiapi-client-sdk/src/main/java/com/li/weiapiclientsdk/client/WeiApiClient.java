package com.li.weiapiclientsdk.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.li.weiapiclientsdk.common.UserInvokeInterfaceRequest;
import com.li.weiapiclientsdk.utils.ClientUtils;

import java.util.HashMap;
import java.util.Map;

public class WeiApiClient {
	private String accessKey;
	private String secretKey;

	public WeiApiClient(String accessKey, String secretKey) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
	}

	/**
	 * 通用调用接口
	 * @param userInvokeInterfaceRequest
	 * @return
	 */
	public Map<String, Object> generalInvokeInterface(UserInvokeInterfaceRequest userInvokeInterfaceRequest) {
		String method = userInvokeInterfaceRequest.getMethod();

		if (method.equals("GET")) {
			return getInvokeInterface(userInvokeInterfaceRequest);
		} else if (method.equals("POST")) {
			return postInvokeInterface(userInvokeInterfaceRequest);
		}

		return null;
	}

	/**
	 * get请求调用接口
	 * @param userInvokeInterfaceRequest
	 * @return
	 */
	private Map<String, Object> getInvokeInterface(UserInvokeInterfaceRequest userInvokeInterfaceRequest) {
		String url = userInvokeInterfaceRequest.getUrl();

		String params = userInvokeInterfaceRequest.getUserParams();
		Map<String, Object> paramMap = JSONUtil.parse(params).toBean(HashMap.class);

		String result = HttpRequest.get(url)
				.addHeaders(ClientUtils.getMap(accessKey, secretKey, params))
				.form(paramMap)
				.execute().body();

		return JSONUtil.parse(result).toBean(Map.class);
	}

	/**
	 * post请求调用接口
	 * @param userInvokeInterfaceRequest
	 * @return
	 */
	private Map<String, Object> postInvokeInterface(UserInvokeInterfaceRequest userInvokeInterfaceRequest) {
		String url = userInvokeInterfaceRequest.getUrl();
		String params = userInvokeInterfaceRequest.getUserParams();

		String result = HttpRequest.post(url)
				.addHeaders(ClientUtils.getMap(accessKey, secretKey, params))
				.body(params)
				.execute().body();

		return JSONUtil.parse(result).toBean(Map.class);
	}
}
