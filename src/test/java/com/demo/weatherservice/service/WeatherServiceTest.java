package com.demo.weatherservice.service;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.demo.weatherservice.dto.WeatherRequest;
import com.demo.weatherservice.dto.WeatherStats;
import com.demo.weatherservice.dto.WeatherStatsResponse;
import com.demo.weatherservice.exception.DataNotFoundException;
import com.demo.weatherservice.service.openweather.OpenWeatherMapService;

@RunWith(MockitoJUnitRunner.class)
public class WeatherServiceTest {

	@InjectMocks
	private WeatherService weatherService;

	@Mock
	private OpenWeatherMapService openWeatherMapService;

	@Test
	public void getWeatherStats_validcase() {
		WeatherRequest weatherRequest = new WeatherRequest("London,us");
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		WeatherStatsResponse weatherStatsResponse = new WeatherStatsResponse(
				Arrays.asList(new WeatherStats(1, 2, 3, tomorrow), new WeatherStats(1, 2, 3, tomorrow.plusDays(1)),
						new WeatherStats(1, 2, 3, tomorrow.plusDays(2))));

		when(openWeatherMapService.getWeatherStats(weatherRequest)).thenReturn(weatherStatsResponse);

		WeatherStatsResponse weatherStats = weatherService.getWeatherStats(weatherRequest);

		Assert.assertNotNull("Expected stats must not be null", weatherStats);

		Assert.assertEquals("3 Days weather stats are expected", weatherStats.getWeatherStats().size(), 3);

		Assert.assertEquals("Tomorrow's weather stats is expected", weatherStats.getWeatherStats().get(0).getDate(),
				tomorrow);

		Assert.assertEquals("Tomorrow's + 1 day weather stats is expected",
				weatherStats.getWeatherStats().get(1).getDate(), tomorrow.plusDays(1));

		Assert.assertEquals("Tomorrow's + 2 day weather stats is expected",
				weatherStats.getWeatherStats().get(2).getDate(), tomorrow.plusDays(2));
	}

	@Test(expected = DataNotFoundException.class)
	public void getWeatherStats_testForException() {
		WeatherRequest weatherRequest = new WeatherRequest("London,us");
		when(openWeatherMapService.getWeatherStats(weatherRequest)).thenThrow(new DataNotFoundException());
		weatherService.getWeatherStats(weatherRequest);
	}
}
