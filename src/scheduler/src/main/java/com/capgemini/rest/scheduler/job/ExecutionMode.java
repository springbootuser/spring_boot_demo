package com.capgemini.rest.scheduler.job;

public enum ExecutionMode {
	SYNCHRONIZED("Synchronized"),
	ASYNCHRONIZED("Asynchronized");
	
	private final String mode;
	
	ExecutionMode(String s) {
		this.mode = s;
	}
	
	public String getMode() {
		return mode;
	}
}
