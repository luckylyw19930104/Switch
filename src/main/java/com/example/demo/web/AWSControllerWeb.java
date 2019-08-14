package com.example.demo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AWSControllerService;

@RestController
@RequestMapping("EC2")
public class AWSControllerWeb {
	
	@Autowired
	AWSControllerService acs;
	
	@PostMapping("stop")
	public void stopEC2() {
		acs.stopEC2("i-0bbf4fb2cc1e2e760");
	}
	
	@PostMapping("start")
	public void startEC2() {
		acs.startEC2("i-0bbf4fb2cc1e2e760");
	}
	
	@PostMapping("get")
	public void get() {
		acs.get();
	}
	
	@GetMapping("test")
	public String test() {
		return "test";
	}
}
