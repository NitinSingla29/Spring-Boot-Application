package com.demo.weatherservice.controller;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.demo.weatherservice.dto.WeatherRequest;
import com.demo.weatherservice.dto.WeatherStats;
import com.demo.weatherservice.dto.WeatherStatsResponse;
import com.demo.weatherservice.exception.DataNotFoundException;
import com.demo.weatherservice.service.WeatherService;

@RunWith(SpringRunner.class)
@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private WeatherService service;

	@Test
	public void testForValidCity() throws Exception {
		String city = "London,us";
		when(service.getWeatherStats(any(WeatherRequest.class))).thenReturn(
				new WeatherStatsResponse(Arrays.asList(new WeatherStats(), new WeatherStats(), new WeatherStats())));

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/data").param("q", city)
				.contentType(MediaType.APPLICATION_JSON);
		mvc.perform(requestBuilder).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("weatherStats").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("weatherStats[0].dayAvgTemp").isNotEmpty());
	}

	@Test
	public void testDataNotFoundException() throws Exception {
		String errorMessage = "Weather Stats is not available";
		when(service.getWeatherStats(any(WeatherRequest.class))).thenThrow(new DataNotFoundException(errorMessage));

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/data").param("q", "1")
				.contentType(MediaType.APPLICATION_JSON);

		mvc.perform(requestBuilder).andExpect(status().is4xxClientError())
				.andExpect(MockMvcResultMatchers.jsonPath("weatherStats").doesNotExist())
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(HttpStatus.NOT_FOUND.value()))
				.andExpect(MockMvcResultMatchers.jsonPath("message").value(errorMessage));

	}

	@Test
	public void testOtherException() throws Exception {
		String errorMessage = "Failed to find Weather Stats";
		when(service.getWeatherStats(any(WeatherRequest.class))).thenThrow(new RuntimeException(errorMessage));

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/data").param("q", "1")
				.contentType(MediaType.APPLICATION_JSON);

		mvc.perform(requestBuilder).andExpect(status().is5xxServerError())
				.andExpect(MockMvcResultMatchers.jsonPath("weatherStats").doesNotExist())
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
				.andExpect(MockMvcResultMatchers.jsonPath("message").value(containsString(errorMessage)));

	}

}
