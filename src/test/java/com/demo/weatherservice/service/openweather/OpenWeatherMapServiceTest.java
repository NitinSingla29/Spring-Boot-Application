package com.demo.weatherservice.service.openweather;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.demo.weatherservice.dto.WeatherRequest;
import com.demo.weatherservice.dto.WeatherStats;
import com.demo.weatherservice.dto.WeatherStatsResponse;
import com.demo.weatherservice.exception.DataNotFoundException;
import com.demo.weatherservice.utility.RestClient;

@RunWith(MockitoJUnitRunner.class)
public class OpenWeatherMapServiceTest {

	@InjectMocks
	private OpenWeatherMapService openWeatherMapService;

	@Mock
	private RestClient restClient;

	@Mock
	private OpenWeatherResponseTransformer openWeatherResponseTransformer;

	@Test
	public void getWeatherStats_validcase() {
		WeatherRequest weatherRequest = new WeatherRequest("London,us");
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		WeatherStatsResponse weatherStatsResponse = new WeatherStatsResponse(
				Arrays.asList(new WeatherStats(1, 2, 3, tomorrow), new WeatherStats(1, 2, 3, tomorrow.plusDays(1)),
						new WeatherStats(1, 2, 3, tomorrow.plusDays(2))));

		OpenWeatherServiceResponse openWeatherServiceResponse = new OpenWeatherServiceResponse();

		when(restClient.get(any(), any(), any())).thenReturn(openWeatherServiceResponse);

		when(openWeatherResponseTransformer.apply(openWeatherServiceResponse)).thenReturn(weatherStatsResponse);

		WeatherStatsResponse weatherStats = openWeatherMapService.getWeatherStats(weatherRequest);

		Assert.assertNotNull("Expected stats must not be null", weatherStats);

		Assert.assertEquals("3 Days weather stats are expected", weatherStats.getWeatherStats().size(), 3);

		Assert.assertEquals("Tomorrow's weather stats is expected", weatherStats.getWeatherStats().get(0).getDate(),
				tomorrow);

		Assert.assertEquals("Tomorrow's + 1 day weather stats is expected",
				weatherStats.getWeatherStats().get(1).getDate(), tomorrow.plusDays(1));

		Assert.assertEquals("Tomorrow's + 2 day weather stats is expected",
				weatherStats.getWeatherStats().get(2).getDate(), tomorrow.plusDays(2));
	}

	@Test(expected = HttpClientErrorException.class)
	public void getWeatherStats_testRestException() {
		WeatherRequest weatherRequest = new WeatherRequest("London,us");
		when(restClient.get(any(), any(), any())).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
		openWeatherMapService.getWeatherStats(weatherRequest);
	}

	@Test(expected = DataNotFoundException.class)
	public void getWeatherStats_testDataNotFoundException() {
		WeatherRequest weatherRequest = new WeatherRequest("London,us");
		when(restClient.get(any(), any(), any())).thenThrow(new DataNotFoundException());
		openWeatherMapService.getWeatherStats(weatherRequest);
	}

	@Test(expected = DataNotFoundException.class)
	public void getWeatherStats_testDataNotFoundException2() {
		WeatherRequest weatherRequest = new WeatherRequest("London,us");
		when(restClient.get(any(), any(), any())).thenReturn(null);
		openWeatherMapService.getWeatherStats(weatherRequest);
	}

}
