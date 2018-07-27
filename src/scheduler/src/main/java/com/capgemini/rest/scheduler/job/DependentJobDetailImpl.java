package com.capgemini.rest.scheduler.job;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.PersistJobDataAfterExecution;
import org.quartz.impl.JobDetailImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@PersistJobDataAfterExecution
public class DependentJobDetailImpl extends JobDetailImpl {
	private static final long serialVersionUID = -2529044283140264025L;

	private static String DEPENDENT_JOB_LIST = "DependentJobList";
	private static String EXECUTION_STATE = "ExecutionState";
	private static String ACTION_URI = "ActionUri";
	private static String EXECUTION_MODE = "ExecutionMode";
	private static String CALLBACK_LISTENER = "CallbackListener";
	private static String LOCK = "Lock";

	public DependentJobDetailImpl() {
		getJobDataMap().put(DEPENDENT_JOB_LIST, new ArrayList<>());
		getJobDataMap().put(EXECUTION_STATE, new ArrayList<>());
		getJobDataMap().put(ACTION_URI, new ArrayList<>());
		getJobDataMap().put(EXECUTION_MODE, new ArrayList<>());
		getJobDataMap().put(CALLBACK_LISTENER, new ArrayList<>());
		getJobDataMap().put(LOCK, new HashMap<>());
	}

	public List<DependentJobDetailImpl> getDependentJobDetail() {
		return (List<DependentJobDetailImpl>)getJobDataMap().get(DEPENDENT_JOB_LIST);
	}

	public void setExecutionStatus(ExecutionState state) {
		List<ExecutionState> stateList = (List<ExecutionState>)getJobDataMap().get(EXECUTION_STATE);
		stateList.add(state);
	}

	public ExecutionState getExecutionState() {
		List<ExecutionState> list = (List<ExecutionState>)getJobDataMap().get(EXECUTION_STATE);
		if (CollectionUtils.isEmpty(list) == false) {
			return list.get(list.size() - 1);
		} else {
			return ExecutionState.NOT_YET_EXECUTED;
		}
	}
	
	public void setActionUri(String uri) {
		List<URI> list = (List<URI>)getJobDataMap().get(ACTION_URI);
		try {
			list.add(new URI(uri));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public List<URI> getActionUriList() {
		return (List<URI>)getJobDataMap().get(ACTION_URI);
	}
	
	public void setExecutionMode(ExecutionMode mode) {
		List<ExecutionMode> modeList = (List<ExecutionMode>)getJobDataMap().get(EXECUTION_MODE);
		modeList.clear();
		modeList.add(mode);
	}
	
	public ExecutionMode getExecutionMode() {
		List<ExecutionMode> modeList = (List<ExecutionMode>)getJobDataMap().get(EXECUTION_MODE);
		if (modeList.isEmpty()) {
			return ExecutionMode.SYNCHRONIZED;
		} else {
			return modeList.get(0);
		}
	}
	
	public void setCallbackListener(DependentJobCallbackListener callbackListener) {
		List<DependentJobCallbackListener> list = (List<DependentJobCallbackListener>)getJobDataMap().get(CALLBACK_LISTENER);
		list.add(callbackListener);
		((Map<DependentJobCallbackListener, Object>)getJobDataMap().get(LOCK)).put(callbackListener, new Object());
	}
	
	public List<DependentJobCallbackListener> getCallbackListener() {
		return (List<DependentJobCallbackListener>)getJobDataMap().get(CALLBACK_LISTENER);
	}
	
	public Object getLock(DependentJobCallbackListener callbackListener) {
		return ((Map<DependentJobCallbackListener, Object>)getJobDataMap().get(LOCK)).get(callbackListener);
	}
}
