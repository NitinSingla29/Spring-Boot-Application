package com.demo.weatherservice.dto;

import java.util.List;

public class WeatherStatsResponse {

	public WeatherStatsResponse() {
	}

	private List<WeatherStats> weatherStats;

	public List<WeatherStats> getWeatherStats() {
		return weatherStats;
	}

	public WeatherStatsResponse(List<WeatherStats> weatherStats) {
		super();
		this.weatherStats = weatherStats;
	}

	public void setWeatherStats(List<WeatherStats> weatherStats) {
		this.weatherStats = weatherStats;
	}
}
