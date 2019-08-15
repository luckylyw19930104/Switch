package com.example.demo.service;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JacksonInject.Value;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

@Service
public class AWSControllerService {

	public Ec2Client initEC2() {
		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
			      "AKIAZDDLBNUNNAM2QXO4",
			      "IknYIuzEF8LOL7ZIf1yUoqjLE2D4RFypClFDbpqF");
		
		Ec2Client ec2 = Ec2Client.builder().region(Region.AP_SOUTHEAST_1).credentialsProvider(StaticCredentialsProvider.create(awsCreds)).build();
		return ec2;
	}
	
	public void startEC2(String instance_id) {
		
		Ec2Client ec2 = initEC2();

		StartInstancesRequest request = StartInstancesRequest.builder()
		    .instanceIds(instance_id).build();

		ec2.startInstances(request);
	}
	
	public void stopEC2(String instance_id) {
		
		Ec2Client ec2 = initEC2();
		

		StopInstancesRequest request = StopInstancesRequest.builder()
		    .instanceIds(instance_id).build();

		ec2.stopInstances(request);

	}
	
	public ArrayList<String> getInstanceID(String ip) {
		
		Ec2Client ec2 = initEC2();
		
        boolean done = false;
        ArrayList instanceIDList = new ArrayList<String>();
        Filter filter = Filter.builder().name("tag:Name").values("Test").build();
        
        // snippet-start:[ec2.java2.describe_instances.main]
        String nextToken = null;
        do {
            DescribeInstancesRequest request = DescribeInstancesRequest.builder().filters(filter).build();
            DescribeInstancesResponse response = ec2.describeInstances(request);
            
            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    instanceIDList.add(instance.instanceId());
                }
            }
            nextToken = response.nextToken();

        } while (nextToken != null);
        
        return instanceIDList;        
    }
}
