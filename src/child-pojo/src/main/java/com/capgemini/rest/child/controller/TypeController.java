package com.capgemini.rest.child.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.rest.model.PojoModel;

@RestController
@RequestMapping(value = "/type")
public class TypeController {
	public static final Logger logger = LoggerFactory.getLogger(TypeController.class);
	
	@RequestMapping(value = "/map")
	public Map<String, String> getMap() {
		Map<String, String> map = new HashMap<>();
		map.put("Key1", "Value1");
		map.put("Key2", "Value2");
		map.put("Key3", "Value3");
		logger.info("Map content {}", map);
		return map;
	}
	
	@RequestMapping(value = "/pojo")
	@ResponseBody
	public PojoModel getPojo() {
		PojoModel pojoModel = new PojoModel("Name123", "Value246");
		logger.info("POJO content {}", pojoModel);
		return pojoModel;
	}
}
