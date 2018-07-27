package com.capgemini.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.rest.properties.DemoProperties;

@RestController
public class DemoController {	
	@Autowired
	private DemoProperties properties;

	@Value("#{'${demo.segmentList}'.split(',')}")
	private List<String> segmentList;

	public void setProperties(DemoProperties properties) {
		this.properties = properties;
	}


	@RequestMapping("/")
	@ResponseBody
	public String home() {
//        return "Hello World!";
		return properties.getDemoValue();
	}

	@RequestMapping("/listFromValue")
	@ResponseBody
	public String listFromValue() {
		StringBuilder sb = new StringBuilder();
		segmentList.forEach(s -> sb.append(s));
		return sb.toString();
	}

	@RequestMapping("/listFromYML")
	@ResponseBody
	public List<String> listFromYML() {
		return properties.getDemoList();
	}
}
