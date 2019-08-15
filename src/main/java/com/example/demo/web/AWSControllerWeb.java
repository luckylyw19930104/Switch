package com.example.demo.web;

import java.awt.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AWSControllerService;

@RestController
@RequestMapping("EC2")
public class AWSControllerWeb {
	
	@Value("${aws.access.key.id}")
	private String access_key_id;
	
	@Value("${aws.secret.access.key}")
	private String secret_access_key;
	
	@Autowired
	AWSControllerService acs;
	
	@PostMapping("stop")
	public void stopEC2() {
		ArrayList<String> list = new ArrayList<>();
		list = acs.getInstanceID("Test");
		for(String l : list) {
			System.out.println(l);
			acs.stopEC2(l);
		}
		//acs.stopEC2("i-0bbf4fb2cc1e2e760");
	}
	
	@PostMapping("start")
	public void startEC2() {
		ArrayList<String> list = new ArrayList<>();
		list = acs.getInstanceID("Test");
		for(String l : list) {
			System.out.println(l);
			acs.startEC2(l);
		}
	}
	
	
	@GetMapping("test")
	public String test() {
		return "test";
	}
}
