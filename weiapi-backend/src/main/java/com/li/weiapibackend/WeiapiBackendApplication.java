package com.li.weiapibackend;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.li.weiapibackend.mapper")
@EnableDubbo
public class WeiapiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeiapiBackendApplication.class, args);
	}

}
