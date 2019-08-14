package com.example.demo.service;

import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.stereotype.Service;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.DescribeAddressesRequest;
import com.amazonaws.services.ec2.model.DescribeAddressesResult;

@Service
public class AWSControllerService {
	
	public void startEC2(String instance_id) {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion("ap-southeast-1").build();

        DryRunSupportedRequest<StartInstancesRequest> dry_request =
            () -> {
            StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instance_id);

            return request.getDryRunRequest();
        };

        DryRunResult dry_response = ec2.dryRun(dry_request);

        if(!dry_response.isSuccessful()) {
            System.out.printf(
                "Failed dry run to start instance %s", instance_id);

            throw dry_response.getDryRunResponse();
        }

        StartInstancesRequest request = new StartInstancesRequest()
            .withInstanceIds(instance_id);

        ec2.startInstances(request);

        System.out.printf("Successfully started instance %s", instance_id);
	}
	
	public void stopEC2(String instance_id) {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion("ap-southeast-1").build();

        DryRunSupportedRequest<StopInstancesRequest> dry_request =
            () -> {
            StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(instance_id);

            return request.getDryRunRequest();
        };

        DryRunResult dry_response = ec2.dryRun(dry_request);

        if(!dry_response.isSuccessful()) {
            System.out.printf(
                "Failed dry run to stop instance %s", instance_id);
            throw dry_response.getDryRunResponse();
        }

        StopInstancesRequest request = new StopInstancesRequest()
            .withInstanceIds(instance_id);

        ec2.stopInstances(request);

        System.out.printf("Successfully stop instance %s", instance_id);
	}
	
	public void get() {
		
		AmazonEC2 client = AmazonEC2ClientBuilder.standard().build();
		DescribeAddressesRequest request = new DescribeAddressesRequest().withFilters(new Filter().withName("private-ip-address").withValues("172.31.22.1"));
		DescribeAddressesResult response = client.describeAddresses(request);
	
		for(Address address : response.getAddresses()) {
		    System.out.printf(
		            "Found address with private IP %s" ,
		            
		            address.getInstanceId());
		}
	}
}
