package com.capgemini.rest.scheduler.job;

public class DependentJobCallbackListener {
	
	public void successCallback(DependentJobDetailImpl job) {
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
		}
		
		synchronized (job.getLock(this)) {
			job.setExecutionStatus(ExecutionState.EXECUTED);
			job.getLock(this).notify();
		}
	}
	
	public void failureCallback(DependentJobDetailImpl job) {
		
	}
}
