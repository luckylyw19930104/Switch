package com.example.demo.pojo;

import org.springframework.stereotype.Component;

@Component
public class AWSControllerResponse {

	private Boolean done;
	private String result;
	private Boolean middle;
	
	public Boolean getMiddle() {
		return middle;
	}
	public void setMiddle(Boolean middle) {
		this.middle = middle;
	}
	public Boolean getDone() {
		return done;
	}
	public void setDone(Boolean done) {
		this.done = done;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
}
