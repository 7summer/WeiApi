package com.li.weiapiclientsdk.config;

import com.li.weiapiclientsdk.client.WeiApiClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wei.client")
@ComponentScan
public class WeiApiClientConfig {
	private String accessKey;
	private String secretKey;

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Bean
	public WeiApiClient weiApiClient() {
		return new WeiApiClient(accessKey, secretKey);
	}
}
