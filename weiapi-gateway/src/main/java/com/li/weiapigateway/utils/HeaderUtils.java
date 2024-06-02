package com.li.weiapigateway.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class HeaderUtils {
	/**
	 * 最大间隔5分钟
	 */
	public static final long MAXINTERVAL = 60*5;

	public static boolean validateHeader(HttpHeaders headers, String secretKey) {
		long timestamp = Long.valueOf(headers.getFirst("timestamp"));
		if (System.currentTimeMillis()/1000 - timestamp > MAXINTERVAL) {
			return false;
		}
		int random = Integer.valueOf(headers.getFirst("random"));
		if (random > 1000) {
			return false;
		}
		String sign = headers.getFirst("sign");
		String jsonParams = null;
		try {
			jsonParams = URLDecoder.decode(headers.getFirst("params"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		String realSign = DigestUtils.md5DigestAsHex((jsonParams + "_" + secretKey).getBytes());
		if (realSign.equals(sign) == false) {
			return false;
		}

		return true;
	}
}
