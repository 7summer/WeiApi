package com.li.weiapiinterface.controller;

import com.li.weiapiinterface.Model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/test")
public class TestController {
	@GetMapping("/getName")
	public User getSpecifyName(@RequestParam String name, HttpServletRequest request) {
		User user = new User();
		user.setUsername(name);
		user.setLike("get请求");

		return user;
	}

	@PostMapping("/getName")
	public User postSpecifyName(@RequestParam String name, HttpServletRequest request) {
		User user = new User();
		user.setUsername(name);
		user.setLike("post请求");

		return user;
	}

	@PostMapping("/getUser")
	public User getUser(@RequestBody User user, HttpServletRequest request) {
		return user;
	}
}
