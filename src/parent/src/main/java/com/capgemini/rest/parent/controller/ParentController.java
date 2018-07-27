package com.capgemini.rest.parent.controller;

import java.net.URI;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(path="/parent")
@ConfigurationProperties(prefix = "demo.child.blocking")
public class ParentController {
	private static final Logger logger = LoggerFactory.getLogger(ParentController.class);

	private RestTemplate blockingRestTemplate = new RestTemplate();
	private AsyncRestTemplate nonBlockingRestTemplate = new AsyncRestTemplate(); 
	
	private String scheme;
	private String host;
	private int port;
	private String path;
	
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setPort(int port) {
		this.port = port;
	}	
	
	public void setPath(String path) {
		this.path = path;
	}
	
	@RequestMapping("/blocking")
	public String blockingParent() {
		try {
			logger.info(">>>");
			logger.info("scheme={}, host={}, port={}, path={}, uri={}", scheme, host, port, path);
			URI uri = UriComponentsBuilder.fromPath(path).scheme(scheme).host(host).port(port).build().toUri();
			logger.info("Parent is going to invoke a blocking REST call... {}", uri);
			String s = blockingRestTemplate.getForObject(uri, String.class);
			logger.info("Content from child blocking call: {}", s);
		} catch (RestClientException e) {
			e.printStackTrace();
		}
		return "Parent microservice is executed";
	}

	@RequestMapping("/nonBlocking")
	public String nonBlockingParent() {
		try {
			logger.info(">>>");
			logger.info("scheme={}, host={}, port={}, path={}, uri={}", scheme, host, port, path);
			URI uri = UriComponentsBuilder.fromPath(path).scheme(scheme).host(host).port(port).build().toUri();
			logger.info("Parent is going to invoke a blocking REST call... {}", uri);
			ListenableFuture<ResponseEntity<String>> future = nonBlockingRestTemplate.getForEntity(uri, String.class);
			future.addCallback(new SuccessCallback<ResponseEntity<String>>() {
				@Override
				public void onSuccess(ResponseEntity<String> result) {
					logger.info("Content from child blocking call: {}", result.getBody());
				}
			}, new FailureCallback() {
				@Override
				public void onFailure(Throwable ex) {
					ex.printStackTrace();
				}
			});
		} catch (RestClientException e) {
			e.printStackTrace();
		}
		return "Parent microservice is executed";
	}

	@RequestMapping("/nonBlockingMultiple")
	public String nonBlockingParentMultiple() {
		logger.info(">>>");
		logger.info("scheme={}, host={}, port={}, path={}, uri={}", scheme, host, port, path);
		IntStream.range(1, 6).mapToObj(i -> {
			ListenableFuture<ResponseEntity<String>> future = null;
			try {
				URI uri = UriComponentsBuilder.fromPath(path).scheme(scheme).host(host).port(port).pathSegment(String.valueOf(i)).build().toUri();
				logger.info("Parent is going to invoke a blocking REST call... {}", uri);
				future = nonBlockingRestTemplate.getForEntity(uri, String.class);
				future.addCallback(new SuccessCallback<ResponseEntity<String>>() {
					@Override
					public void onSuccess(ResponseEntity<String> result) {
						logger.info("Content from child blocking call: {}", result.getBody());
					}
				}, new FailureCallback() {
					@Override
					public void onFailure(Throwable ex) {
						ex.printStackTrace();
					}
				});
			} catch (RestClientException e) {
				e.printStackTrace();
			}
			return future;
		}).collect(Collectors.toList());
		return "Parent microservice is executed";
	}
}
