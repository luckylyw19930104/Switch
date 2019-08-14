package com.example.demo.service;

import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
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
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;


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
		
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAZDDLBNUNGHZCBRPI", "2U51EkO5vVWaImH8RAVpJwokbtcUz/1sVvCNr92U");
		
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion("ap-southeast-1").withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

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
		
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
		boolean done = false;

		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while(!done) {
		    DescribeInstancesResult response = ec2.describeInstances(request);

		    for(Reservation reservation : response.getReservations()) {
		        for(Instance instance : reservation.getInstances()) {
		            System.out.printf(
		                "Found instance with id %s, " +
		                "AMI %s, " +
		                "type %s, " +
		                "state %s " +
		                "and monitoring state %s",
		                instance.getInstanceId(),
		                instance.getImageId(),
		                instance.getInstanceType(),
		                instance.getState().getName(),
		                instance.getMonitoring().getState());
		        }
		    }

		    request.setNextToken(response.getNextToken());

		    if(response.getNextToken() == null) {
		        done = true;
		    }
		}
	}
}
