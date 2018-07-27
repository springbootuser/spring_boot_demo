package com.capgemini.rest.child.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/child")
public class ChildController {
	private static final Logger logger = LoggerFactory.getLogger(ChildController.class);
	
	@RequestMapping(path = {"/blocking", "/blocking/{id}"})
	@ResponseBody
	public String blocking(@PathVariable Optional<String> id) {
		String s = "";
		try {
			if (id.isPresent()) {
				s = id.get() + " ";
			}
			logger.info(">> child blocking {}starts", s);
			Thread.sleep(5000);
			logger.info(">> child blocking {}ends", s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return String.format("Child microservice %sis executed", s);
	}
}
