package com.capgemini.rest.child.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.rest.model.PojoModel;

@RestController
@RequestMapping(value = "/type")
public class TypeController {
	
	@RequestMapping(value = "/string")
	public String getString() {
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Test String";
	}
	
	@RequestMapping(value = "/jsonString", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getJsonString() {
		return "abcd";
	}
	
	@RequestMapping(value = "/jsonStringInteger", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getJsonStringInteger() {
		return "1234";
	}
	
	@RequestMapping(value = "/jsonObject", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public PojoModel getJsonObject() {
		return new PojoModel("Vincent", "value1234");
	}
}
