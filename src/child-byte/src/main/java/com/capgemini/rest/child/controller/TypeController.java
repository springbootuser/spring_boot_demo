package com.capgemini.rest.child.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/type")
public class TypeController {
	
	@RequestMapping(value = "/bytes", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public byte[] getBytes() {
		byte[] b = null;
		String s = "This string will be transformed into bytes";
		try {
			b = s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	@RequestMapping(value = "/list")
	public List<byte[]> getList() {
		List<byte[]> list = new ArrayList<>();
		byte[] b = null;
		String s = "This string will be transformed into bytes";
		try {
			b = s.getBytes("UTF-8");
			list.add(b);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@RequestMapping(value = "/encodedBytes")
	public String getEncodedBytes() {
		byte[] b = null;
		String s = "This string will be transformed into bytes";
		try {
			b = s.getBytes("UTF-8");
			return Base64.getEncoder().encodeToString(b);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
