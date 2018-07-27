package com.capgemini.rest.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "demo")
public class DemoProperties {
	
	private List<String> demoList = new ArrayList<>();
	private String demoValue;
	
	public void setDemoList(List<String> demoList) {
		this.demoList = demoList;
	}
	
	public List<String> getDemoList() {
		return demoList;
	}
	
	public void setDemoValue(String demoValue) {
		this.demoValue = demoValue;
	}
	
	public String getDemoValue() {
		return demoValue;
	}
}
