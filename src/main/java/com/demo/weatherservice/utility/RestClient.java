package com.demo.weatherservice.utility;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.demo.weatherservice.exception.DataNotFoundException;
import com.demo.weatherservice.exception.RequestProcessingException;

@Component
public class RestClient {

	private static final String SENSITIVE_APPID_FIELD = "appid";
	@Autowired
	private RestTemplate restTemplate;

	public <T> T get(String url, Map<String, String> params, Class<T> responseType) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
		if (params != null && params.size() > 0) {
			params.forEach((k, v) -> uriBuilder.queryParam(k, v));
		}
		T result;
		try {
			result = restTemplate.getForObject(uriBuilder.toUriString(), responseType, params);
		} catch (HttpClientErrorException e) {
			throw new DataNotFoundException(getErrorMessage("Data not found for the request.", url, params), e);
		} catch (Exception e) {
			throw new RequestProcessingException(
					getErrorMessage("Failed to execute request due to internat server error.", url, params), e);
		}
		return result;
	}

	private String getErrorMessage(String prefix, String url, Map<String, String> params) {
		Map<String, String> nonSensitiveParams = new HashMap<>(params);
		nonSensitiveParams.remove(SENSITIVE_APPID_FIELD);
		return String.format("%s url=[%s] RequestParams=[%s]", prefix, url, nonSensitiveParams.toString());
	}

}
