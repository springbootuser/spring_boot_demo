package com.capgemini.rest.scheduler.job;

public enum ExecutionState {
	EXECUTED("Executed"),
	TO_BE_EXECUTED("ToBeExecuted"),
	NOT_YET_EXECUTED("NotYetExecuted");
	
	private final String state;
	
	ExecutionState(String s) {
		this.state = s;
	}
	
	public String getState() {
		return state;
	}
}
