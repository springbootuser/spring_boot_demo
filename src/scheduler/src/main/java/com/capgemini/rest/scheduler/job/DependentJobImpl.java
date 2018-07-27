package com.capgemini.rest.scheduler.job;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.capgemini.rest.model.PojoModel;

public class DependentJobImpl implements Job {
	private static final Logger logger = LogManager.getLogger(DependentJobImpl.class);
	
	private RestTemplate restTemplate = new RestTemplate();
	private AsyncRestTemplate nonBlockingRestTemplate = new AsyncRestTemplate();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		
		if (jobDetail instanceof DependentJobDetailImpl) {
			DependentJobDetailImpl job = (DependentJobDetailImpl)jobDetail;
			
			String group = context.getJobDetail().getKey().getGroup();
			String name = context.getJobDetail().getKey().getName();
			ExecutionMode mode = job.getExecutionMode();
			logger.info(">>>");
			logger.info(String.format(">> %s:%s starts with %s %s", group, name, mode, job.getActionUriList()));
			
			for (URI path : job.getActionUriList()) {
				try {
					if (mode == ExecutionMode.SYNCHRONIZED) {
						Object jacksonObj = restTemplate.getForObject(path, Object.class);
						genericHandler(path, jacksonObj);
					} else if (mode == ExecutionMode.ASYNCHRONIZED) {
						ListenableFuture<ResponseEntity<Object>> future = nonBlockingRestTemplate.getForEntity(path, Object.class);
						future.addCallback(new SuccessCallback<ResponseEntity<Object>>() {
							@Override
							public void onSuccess(ResponseEntity<Object> result) {
								//HttpHeaders headers = result.getHeaders();
								//headers.keySet().forEach(key -> logger.info("header {key:%s,value:%s}", key, headers.get(key)));
								
								Object jacksonObj = result.getBody();
								genericHandler(path, jacksonObj);
								job.getCallbackListener().forEach(callbackListener -> callbackListener.successCallback(job));
							}
						}, new FailureCallback() {
							@Override
							public void onFailure(Throwable ex) {
								byteArrayHandler(path, mode);
								job.getCallbackListener().forEach(callbackListener -> callbackListener.failureCallback(job));
							}
						});
					}
				} catch (HttpMessageNotReadableException e) {
					byteArrayHandler(path, mode);
				} catch (RestClientException e) {
					e.printStackTrace();
				}
			}
			
//			try {
//				Thread.sleep(2000 + new Random().nextInt(5) * 2000);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			
			logger.info(String.format(">> %s:%s ends", group, name));
		}
	}
	
	private void genericHandler(URI path, Object jacksonObj) {
		try {
			logger.info(String.format("Content from \"%s\": %s", path, jacksonObj));
			Object obj = new ObjectMapper().convertValue(jacksonObj, jacksonObj.getClass());
			
			if (obj instanceof Map) {
				Map<?, ?> map = (Map<?, ?>) obj;
				logger.info(String.format(">> Map content from [%s]: %s", path, map));
//				map.keySet().forEach(key -> {
//					logger.info("header {key=%s,value=%s}", key, map.get(key));
//				});
			} else if (obj instanceof String) {
				String s = (String) obj;
				logger.info(String.format(">> String content from [%s]: %s", path, s));
			} else if (obj instanceof Integer) {
				Integer i = (Integer) obj;
				logger.info(String.format(">> Integer content from [%s]: %d", path, i));
			} else if (obj instanceof PojoModel) {
				PojoModel model = (PojoModel) obj;
				logger.info(String.format(">> POJO content from [%s]: {name=%s,value=%s}", path, model.getName(), model.getValue()));
			} else if (obj instanceof byte[]) {
				try {
					logger.info(String.format(">> byte[] String content from [%s]: %s", path, new String((byte[]) obj, "UTF-8")));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else if (obj instanceof List) {
				List<?> list = (List<?>) obj;
				list.forEach(element -> {
					if (element instanceof byte[]) {
						byte[] b = (byte[]) element;
						try {
							logger.info(String.format(">> byte[] list content from [%s]: %s", path, new String(b, "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					} else if (element instanceof String) {
						String s = (String) element;
						logger.info(String.format(">> String list content from [%s]: %s", path, s));
					} else {
						logger.info(String.format("Unknown list content from [%s]: {class=%s,toString=%s}", path, element.getClass(), element));
					}
				});
			} else {
				logger.info(String.format(">> Unknown content from [%s]: {class=%s,toString=%s}", path, obj.getClass(), obj));
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	private void byteArrayHandler(URI path, ExecutionMode mode) {
		if (ExecutionMode.SYNCHRONIZED == mode) {
			byte[] b = restTemplate.getForObject(path, byte[].class);
			try {
				logger.info(String.format(">> byte[] string content from [%s]: %s", path, new String(b, "UTF-8")));
				//logger.info(">> byte[] decoded string content from [%s]: %s", path, new String(Base64.getDecoder().decode(new String(b, "UTF-8")), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (ExecutionMode.ASYNCHRONIZED == mode) {
			ListenableFuture<ResponseEntity<byte[]>> future = nonBlockingRestTemplate.getForEntity(path, byte[].class);
			future.addCallback(new SuccessCallback<ResponseEntity<byte[]>>() {
				@Override
				public void onSuccess(ResponseEntity<byte[]> result) {
					try {
						logger.info(String.format(">> byte[] string content from [%s]: %s", path, new String(result.getBody(), "UTF-8")));
						// logger.info(">> byte[] decoded string content from [%s]: %s", path, new String(Base64.getDecoder().decode(new String(result.getBody(), "UTF-8")), "UTF-8"));
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
}
