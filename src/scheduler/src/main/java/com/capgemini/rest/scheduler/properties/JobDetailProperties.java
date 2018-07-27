package com.capgemini.rest.scheduler.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import com.capgemini.rest.scheduler.job.ExecutionMode;
import com.capgemini.rest.scheduler.job.ExecutionState;

@Configuration
public class JobDetailProperties {
	public static String GROUP = "Group";
	public static String NAME = "Name";
	private static String GROUP_NAME = "GroupName";
	private static String EXECUTION_STATE = "ExecutionState";
	private static String DEPENDENT_JOB_DETAIL_MAP = "DependentJobDetailMap";
	private static String ACTION_URI = "ActionUri";
	private static String EXECUTION_MODE = "ExecutionMode";
	
	private Map<String, Object> jobDetailMap = new HashMap<>();
	
	public void setJobDetailMap(Map<String, Object> jobDetailMap) {
		this.jobDetailMap = jobDetailMap;
	}
	
	public Map<String, Object> getJobDetailMap() {
		return jobDetailMap;
	}
	
	public String getJobGroup() {
		return (String)((Map<String, String>) jobDetailMap.get(GROUP_NAME)).get(GROUP);
	}
	
	public String getJobName() {
		return (String)((Map<String,String>) jobDetailMap.get(GROUP_NAME)).get(NAME);
	}
	
	public ExecutionState getJobExecutionState() {
		return ExecutionState.valueOf((String)jobDetailMap.get(EXECUTION_STATE));
	}
	
	public Map<Integer, Map<String, String>> getDependentJobDetailMap() {
		return (Map<Integer, Map<String, String>>) jobDetailMap.get(DEPENDENT_JOB_DETAIL_MAP);
	}
	
	public List<String> getActionUri() {
		Map<Integer, String> map = (Map<Integer, String>)jobDetailMap.get(ACTION_URI);
		if (CollectionUtils.isEmpty(map) == false) {
			return map.values().stream().collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}
	
	public ExecutionMode getExecutionMode() {
		return ExecutionMode.valueOf((String)jobDetailMap.get(EXECUTION_MODE));
	}
	
	@Override
	public String toString() {
		return String.format("jobDetailMap={%s}", jobDetailMap);
	}
}
