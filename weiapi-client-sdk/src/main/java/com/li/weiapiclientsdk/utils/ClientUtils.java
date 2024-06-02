package com.li.weiapiclientsdk.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ClientUtils {
	/**
	 * 得到HeaderMap
	 * @param accessKey
	 * @param secretKey
	 * @param body
	 * @return
	 */
	public static Map<String, String> getMap(String accessKey, String secretKey, String body) {
		Map<String, String> headerMap = new HashMap<String, String>();

		headerMap.put("accessKey", accessKey);
		headerMap.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
		headerMap.put("random", String.valueOf(RandomUtil.randomInt(1000)));
		headerMap.put("sign", getSign(body, secretKey));

		try {
			headerMap.put("params", URLEncoder.encode(body, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		return headerMap;
	}

	/**
	 * 使用请求参数+密钥生成签名
	 * @return
	 */
	private static String getSign(String body, String secretKey) {
		Digester md5 = new Digester(DigestAlgorithm.MD5);

		return md5.digestHex(body + "_" + secretKey);
	}
}
