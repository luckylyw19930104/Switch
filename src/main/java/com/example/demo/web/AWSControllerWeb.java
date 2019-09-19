package com.example.demo.web;

import java.awt.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.pojo.AWSControllerResponse;
import com.example.demo.service.AWSControllerAsyncService;

import software.amazon.awssdk.services.ec2.Ec2AsyncClient;

@RestController
@RequestMapping("EC2")
public class AWSControllerWeb {
	
	@Value("${aws.access.key.id}")
	private String access_key_id;
	
	@Value("${aws.secret.access.key}")
	private String secret_access_key;
	
	@Autowired
	AWSControllerAsyncService acas;
	
	@Autowired
	AWSControllerResponse acr;
	
	/**
	 * Rest API for stop specific EC2
	 * @param ip
	 * @return
	 */
	@PostMapping("stop")
	public AWSControllerResponse stopEC2(@RequestParam String ip) {
		// initial EC2 Async client
		Ec2AsyncClient client = acas.initAsync(access_key_id, secret_access_key);
		// describe information (instance ID, state and so on) for specific EC2
		AWSControllerResponse result = acas.describeAsync(client, ip, "stopped");
		// boolean value for middle mean need to connect to AWS or not
		if(!acr.getMiddle()) {
			return result;
		}else {
			// stop specific EC2
			acas.stopAsync(client, acr.getResult());
			acr.setResult("Specific EC2 has been stopped");
			return acr;
		}
		//acs.stopEC2("i-0bbf4fb2cc1e2e760");
	}
	
	@PostMapping("start")
	public AWSControllerResponse startEC2(@RequestParam String ip) {
		Ec2AsyncClient client = acas.initAsync(access_key_id, secret_access_key);
		AWSControllerResponse result = acas.describeAsync(client, ip, "running");
		if(!acr.getMiddle()) {
			return result;
		}else {
			// start specific EC2 on AWS
			acas.startAsync(client, acr.getResult());
			acr.setResult("Specific EC2 has been started");
			return acr;
		}
	}
}
