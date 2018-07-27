package com.capgemini.rest.model;

public class PojoModel {
	
	private String name;
	private String value;
	
	public PojoModel() {
	}
	
	public PojoModel(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
