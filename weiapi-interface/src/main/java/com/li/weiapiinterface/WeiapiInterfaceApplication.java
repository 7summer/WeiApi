package com.li.weiapiinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class WeiapiInterfaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeiapiInterfaceApplication.class, args);
	}

}
