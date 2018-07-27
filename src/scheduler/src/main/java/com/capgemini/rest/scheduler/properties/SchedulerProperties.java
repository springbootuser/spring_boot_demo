package com.capgemini.rest.scheduler.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "demo.scheduler")
public class SchedulerProperties {
	
	@Autowired
	private List<JobDetailProperties> jobList = new ArrayList<>();
	
	public void setJobList(List<JobDetailProperties> jobList) {
		this.jobList = jobList;
	}
	
	public List<JobDetailProperties> getJobList() {
		return jobList;
	}
}
