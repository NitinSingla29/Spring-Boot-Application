package com.demo.weatherservice.service.openweather;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.demo.weatherservice.dto.WeatherRequest;
import com.demo.weatherservice.dto.WeatherStatsResponse;
import com.demo.weatherservice.exception.DataNotFoundException;
import com.demo.weatherservice.utility.RestClient;

@Component
public class OpenWeatherMapService {

	@Value("${weather.data.provider.url}")
	private String weatherDataServiceUrl;

	@Value("${weather.data.provider.appid}")
	private String appId;

	@Autowired
	private RestClient restClient;

	@Autowired
	private OpenWeatherResponseTransformer openWeatherResponseTransformer;

	@SuppressWarnings("serial")
	public WeatherStatsResponse getWeatherStats(WeatherRequest weatherRequest) {
		Map<String, String> queryParamMap = new HashMap<String, String>() {
			{
				put("q", weatherRequest.getCity());
				put("appid", appId);
			}
		};
		OpenWeatherServiceResponse openWeatherServiceResponse = restClient.get(weatherDataServiceUrl, queryParamMap,
				OpenWeatherServiceResponse.class);
		if (openWeatherServiceResponse == null) {
			throw new DataNotFoundException(
					"Weather Statistics are not found for city [" + weatherRequest.getCity() + "]");
		}
		return openWeatherResponseTransformer.apply(openWeatherServiceResponse);

	}
}
