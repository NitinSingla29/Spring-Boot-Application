package com.demo.weatherservice.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.weatherservice.dto.WeatherRequest;
import com.demo.weatherservice.dto.WeatherStatsResponse;
import com.demo.weatherservice.service.openweather.OpenWeatherMapService;

@Service
public class WeatherService {

  @Autowired
  private OpenWeatherMapService weatherMapService;


  public WeatherStatsResponse getWeatherStats(WeatherRequest weatherRequest) {
    return weatherMapService.getWeatherStats(weatherRequest);
  }
}
