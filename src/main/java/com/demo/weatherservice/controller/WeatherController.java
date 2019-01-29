package com.demo.weatherservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.weatherservice.dto.WeatherRequest;
import com.demo.weatherservice.dto.WeatherStatsResponse;
import com.demo.weatherservice.service.WeatherService;

@RestController
@RequestMapping
public class WeatherController {

	@Autowired
	private WeatherService weatherService;

	@GetMapping("/data")
	public WeatherStatsResponse weatherStats(@RequestParam("q") String city) {
		return weatherService.getWeatherStats(new WeatherRequest(city));
	}

}
