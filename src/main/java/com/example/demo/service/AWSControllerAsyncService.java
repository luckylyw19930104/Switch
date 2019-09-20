package com.example.demo.service;

import java.util.ArrayList;

import java.util.concurrent.CompletableFuture;

import org.apache.commons.logging.Log;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.pojo.AWSControllerResponse;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StartInstancesResponse;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StopInstancesResponse;


import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Service
public class AWSControllerAsyncService {
	
	@Autowired
	AWSControllerResponse acr;
	
	
	/**
	 * initial EC2 Async client for further operation
	 * @param access_key_id
	 * @param secret_access_key
	 * @return
	 */
	public Ec2AsyncClient initAsync(String access_key_id, String secret_access_key) {
		// set credentials of AWS account (User in IAM)
		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
			      access_key_id,
			      secret_access_key);
		// set avaliable region for your AWS account
		
		Ec2AsyncClient client = Ec2AsyncClient.builder().region(Region.AP_SOUTHEAST_1)
					.credentialsProvider(StaticCredentialsProvider.create(awsCreds)).build();
		
		log.info("Connection with access key id: {}, secret access key: {} in region: SOUTHEAST_1", access_key_id, secret_access_key);
		
		return client;
	}
	
	/**
	 * 
	 * @param client
	 * @param instanceID
	 */
	public void startAsync(Ec2AsyncClient client, String instanceID) {
		//Ec2AsyncClient client = initAsync();
		log.info("Start EC2 with instance ID {}", instanceID);
		StartInstancesRequest request = StartInstancesRequest.builder()
			    .instanceIds(instanceID).build();
		
		CompletableFuture<StartInstancesResponse> future = client.startInstances(request);
		
		future.whenComplete((resp, err) -> {
			try {
				if (resp != null) {
					do {
						//System.out.println(stateInspection(client, instanceID));
					}while(!stateInspection(client, instanceID).equals("running"));
				}else {
					err.printStackTrace();
				}
			}finally {
				client.close();
				log.info("Specific EC2 has been started");
			}
		});
		future.join();
	}
	
	/**
	 * 
	 * @param client
	 * @param instanceID
	 */
	public void stopAsync(Ec2AsyncClient client, String instanceID) {
		//Ec2AsyncClient client = initAsync();
		log.info("Stop EC2 with instance ID {}", instanceID);
		StopInstancesRequest request = StopInstancesRequest.builder()
			    .instanceIds(instanceID).build();
		
		CompletableFuture<StopInstancesResponse> future = client.stopInstances(request);
		
		future.whenComplete((resp, err) -> {
			try {
				if (resp != null) {
					do {
						//System.out.println(stateInspection(client, instanceID));
					}while(!stateInspection(client, instanceID).equals("stopped"));
				}else {
					err.printStackTrace();
				}
			}finally {
				client.close();
				log.info("Specific EC2 has been stopped");
			}
		});
		future.join();
	}
	
	/**
	 * 
	 * @param client
	 * @param ip
	 * @param command
	 * @return
	 */
	public AWSControllerResponse describeAsync(Ec2AsyncClient client, String ip, String command) {
		//Ec2AsyncClient client = initAsync();
		
		boolean done = false;
        //ArrayList instanceIDList = new ArrayList<String>();
		log.info("Get instance ID with private address: {}", ip);
        Filter filter = Filter.builder().name("private-ip-address").values(ip).build();
        
        String nextToken = null;
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().filters(filter).build();
        CompletableFuture<DescribeInstancesResponse> future = client.describeInstances(request);
        DescribeInstancesResponse response = future.join();
        try {
        	
        	Instance instance = response.reservations().get(0).instances().get(0);
        
        	final String state = instance.state().nameAsString();
        	log.info("Current state of specific EC2 is {}", state);
//        System.out.println(command);
//        System.out.println(state);
        	
        	// check whether EC2 at the same state of command
        	if(state.equals(command)) {
        		acr.setResult("The state of specific EC2 is " + command + ", nothing to do!");
        		acr.setMiddle(false);
        		acr.setDone(true);
        		return acr;
        	}
        	
        	// return the instance ID for specific private IP
        	String instanceID = instance.instanceId();
        	log.info("Instance ID of specific EC2 is {}", instanceID);
        //System.out.println(instanceID);
        	acr.setResult(instanceID);
        	acr.setMiddle(true);
        	acr.setDone(true);
        	return acr;
        	
        	// in case user enter the wrong ip address
        	}catch(IndexOutOfBoundsException e){
        		e.printStackTrace();
        		acr.setResult("Please check your input ip address!");
        		acr.setMiddle(false);
        		acr.setDone(false);
        		log.error("Can not find EC2 with private IP address {}", ip);
        		return acr;
        }
	}
	
	/**
	 * 
	 * @param client
	 * @param instanceID
	 * @return
	 */
	public String stateInspection(Ec2AsyncClient client, String instanceID) {
		
		Filter filter = Filter.builder().name("instance-id").values(instanceID).build();
        
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().filters(filter).build();
        CompletableFuture<DescribeInstancesResponse> future = client.describeInstances(request);
        DescribeInstancesResponse response = future.join();
        
        Instance instance = response.reservations().get(0).instances().get(0);
        String state = instance.state().nameAsString();
        acr.setResult(state);
        return state;
	}
}
