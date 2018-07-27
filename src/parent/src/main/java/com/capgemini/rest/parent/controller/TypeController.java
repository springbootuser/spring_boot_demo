package com.capgemini.rest.parent.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.capgemini.rest.model.PojoModel;

@RestController
@RequestMapping(path = "/parent/")
@ConfigurationProperties("demo.child.type")
public class TypeController {
	private static final Logger logger = LoggerFactory.getLogger(TypeController.class);

	private AsyncRestTemplate nonBlockingRestTemplate = new AsyncRestTemplate();
	
	private URI stringPath;
	private URI mapPath;
	private URI pojoPath;
	private URI jsonStringPath;
	private URI jsonObjectPath;
	private URI bytePath;
	private URI genericPath;
	
	public void setStringPath(URI stringPath) {
		this.stringPath = stringPath;
	}
	
	public void setMapPath(URI mapPath) {
		this.mapPath = mapPath;
	}
	
	public void setPojoPath(URI pojoPath) {
		this.pojoPath = pojoPath;
	}
	
	public void setJsonStringPath(URI jsonStringPath) {
		this.jsonStringPath = jsonStringPath;
	}
	
	public void setJsonObjectPath(URI jsonObjectPath) {
		this.jsonObjectPath = jsonObjectPath;
	}
	
	public void setBytePath(URI bytePath) {
		this.bytePath = bytePath;
	}
	
	public void setGenericPath(URI genericPath) {
		this.genericPath = genericPath;
	}

	@RequestMapping(path="/string")
	public String getString() {
		try {
			logger.info(">>>");
			logger.info("Parent is going to invoke a child microservice...");
			ListenableFuture<ResponseEntity<String>> future = nonBlockingRestTemplate.getForEntity(stringPath, String.class);
			future.addCallback(new SuccessCallback<ResponseEntity<String>>() {
				@Override
				public void onSuccess(ResponseEntity<String> result) {
					logger.info("String content from child microservice: {}", result.getBody());
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

	@RequestMapping(path="/map")
	public String getMap() {
		try {
			logger.info(">>>");
			logger.info("Parent is going to invoke a child microservice...");
			ListenableFuture<ResponseEntity<Map>> future = nonBlockingRestTemplate.getForEntity(mapPath, Map.class);
			future.addCallback(new SuccessCallback<ResponseEntity<Map>>() {
				@Override
				public void onSuccess(ResponseEntity<Map> result) {
					Map<?, ?> map = (Map<?, ?>) result.getBody();
					logger.info("Map content from child microservice: ");
					map.keySet().forEach(key -> logger.info("header {key={},value={}}", key, map.get(key)));
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
	
	@RequestMapping(path="/pojo")
	public String getPojo() {
		try {
			logger.info(">>>");
			logger.info("Parent is going to invoke a child microservice...");
			ListenableFuture<ResponseEntity<PojoModel>> future = nonBlockingRestTemplate.getForEntity(pojoPath, PojoModel.class);
			future.addCallback(new SuccessCallback<ResponseEntity<PojoModel>>() {
				@Override
				public void onSuccess(ResponseEntity<PojoModel> result) {
					PojoModel model = result.getBody();
					logger.info("POJO content from child microservice: {name={},value={}}", model.getName(), model.getValue());
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

	@RequestMapping(path="/jsonString")
	public String getJsonString() {
		try {
			logger.info(">>>");
			logger.info("Parent is going to invoke a child microservice...");
			ListenableFuture<ResponseEntity<String>> future = nonBlockingRestTemplate.getForEntity(jsonStringPath, String.class);
			future.addCallback(new SuccessCallback<ResponseEntity<String>>() {
				@Override
				public void onSuccess(ResponseEntity<String> result) {
					logger.info("String content(JSON) from child microservice: {}", result.getBody());
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

	@RequestMapping(path="/jsonObject")
	public String getJsonObject() {
		try {
			logger.info(">>>");
			logger.info("Parent is going to invoke a child microservice...");
			ListenableFuture<ResponseEntity<PojoModel>> future = nonBlockingRestTemplate.getForEntity(jsonObjectPath, PojoModel.class);
			future.addCallback(new SuccessCallback<ResponseEntity<PojoModel>>() {
				@Override
				public void onSuccess(ResponseEntity<PojoModel> result) {
					PojoModel model = result.getBody();
					logger.info("POJO content(JSON) from child microservice: {name={},value={}}", model.getName(), model.getValue());
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

	@RequestMapping(path="/bytes")
	public String getBytes() {
		try {
			logger.info(">>>");
			logger.info("Parent is going to invoke a child microservice...");
			ListenableFuture<ResponseEntity<byte[]>> future = nonBlockingRestTemplate.getForEntity(bytePath, byte[].class);
			future.addCallback(new SuccessCallback<ResponseEntity<byte[]>>() {
				@Override
				public void onSuccess(ResponseEntity<byte[]> result) {
					try {
						logger.info("byte[] content from child microservice: {}", new String(result.getBody(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
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

	@RequestMapping(path = {"/generic", "/generic/{action}"})
	public String getGeneric(@PathVariable Optional<String> action) {
		try {
			logger.info(">>>");
			logger.info("Parent is going to invoke a child microservice...");
			String childPath = "bytes";
			if (action.isPresent()) {
				childPath = action.get();
			}
			final URI path = UriComponentsBuilder.fromUri(genericPath).path(childPath).build().toUri();
			ListenableFuture<ResponseEntity<Object>> future = nonBlockingRestTemplate.getForEntity(path, Object.class);
			future.addCallback(new SuccessCallback<ResponseEntity<Object>>() {
				@Override
				public void onSuccess(ResponseEntity<Object> result) {
					HttpHeaders headers = result.getHeaders();
					headers.keySet().forEach(key -> logger.info("header {key:{},value:{}}", key, headers.get(key)));
					
					try {
						Object jacksonObj = result.getBody();
						logger.info("Content class: {}", jacksonObj);
						Object obj = new ObjectMapper().convertValue(jacksonObj, jacksonObj.getClass());
						
						if (obj instanceof Map) {
							Map<?, ?> map = (Map<?, ?>) obj;
							logger.info("Map content from child microservice: ");
							map.keySet().forEach(key -> {
								logger.info("header {key={},value={}}", key, map.get(key));
							});
						} else if (obj instanceof String) {
							String s = (String) obj;
							logger.info("String content from child microservice: {}", s);
						} else if (obj instanceof Integer) {
							Integer i = (Integer) obj;
							logger.info("String content from child microservice: {}", i);
						} else if (obj instanceof PojoModel) {
							PojoModel model = (PojoModel) obj;
							logger.info("POJO content from child microservice: {name={},value={}}", model.getName(), model.getValue());
						} else if (obj instanceof byte[]) {
							try {
								logger.info("byte[] String content from child microservice: {}", new String((byte[]) obj, "UTF-8"));
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						} else if (obj instanceof List) {
							List<?> list = (List<?>) obj;
							list.forEach(element -> {
								if (element instanceof byte[]) {
									byte[] b = (byte[]) element;
									try {
										logger.info("byte[] list content from child microservice: {}", new String(b, "UTF-8"));
									} catch (UnsupportedEncodingException e) {
										e.printStackTrace();
									}
								} else if (element instanceof String) {
									String s = (String) element;
									logger.info("String list content from child microservice: {}", s);
								} else {
									logger.info("Unknown list content from child microservice: {class={},toString={}}", element.getClass(), element);
								}
							});
						} else {
							logger.info("Unknown content from child microservice: {class={},toString={}}", obj.getClass(), obj);
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}, new FailureCallback() {
				@Override
				public void onFailure(Throwable ex) {
					logger.info("Generic handler got failure: {}", ex.getLocalizedMessage());
					byteArrayHandler(path);
				}
			});
		} catch (RestClientException e) {
			e.printStackTrace();
		}
		return "Parent microservice is executed";
	}
	
	private void byteArrayHandler(URI path) {
		ListenableFuture<ResponseEntity<byte[]>> future = nonBlockingRestTemplate.getForEntity(path, byte[].class);
		future.addCallback(new SuccessCallback<ResponseEntity<byte[]>>() {
			@Override
			public void onSuccess(ResponseEntity<byte[]> result) {
				try {
					logger.info("byte[] string content from child microservice: {}", new String(result.getBody(), "UTF-8"));
					logger.info("byte[] decoded string content from child microservice: {}", new String(Base64.getDecoder().decode(new String(result.getBody(), "UTF-8")), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}, new FailureCallback() {
			@Override
			public void onFailure(Throwable ex) {
				ex.printStackTrace();
			}
		});
	}

}
