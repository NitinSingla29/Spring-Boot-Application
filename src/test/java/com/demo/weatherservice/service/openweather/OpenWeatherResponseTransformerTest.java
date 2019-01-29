package com.demo.weatherservice.service.openweather;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.demo.weatherservice.dto.WeatherStatsResponse;
import com.demo.weatherservice.service.openweather.model.Main;
import com.demo.weatherservice.service.openweather.model.WeatherDetail;

@RunWith(MockitoJUnitRunner.class)
public class OpenWeatherResponseTransformerTest {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

	@InjectMocks
	OpenWeatherResponseTransformer openWeatherResponseTransformer;

	@Test(expected = NullPointerException.class)
	public void apply_testWithBlankResponse() {
		openWeatherResponseTransformer.apply(new OpenWeatherServiceResponse());
	}

	@Test(expected = NullPointerException.class)
	public void apply_testForNullInput() {
		openWeatherResponseTransformer.apply(null);
	}

	@Test
	public void testForValidInputResponses() {
		WeatherStatsResponse weatherStatsResponse = openWeatherResponseTransformer
				.apply(sampleValidResponseWithPastData());
		Assert.assertNotNull("Expected stats must not be null", weatherStatsResponse);
		// There will be no stas as there is no weather details in input
		// response for next 3 days.
		Assert.assertEquals(0, weatherStatsResponse.getWeatherStats().size());

		// Test with Input response having future weather details
		weatherStatsResponse = openWeatherResponseTransformer.apply(sampleValidResponseForFuture());
		Assert.assertNotNull("Expected stats must not be null", weatherStatsResponse);
		// There will be 3 days stas.
		Assert.assertEquals("Next 3 days of data from today must be present", 3,
				weatherStatsResponse.getWeatherStats().size());
		Assert.assertEquals("Today + 3rd data must be present", LocalDate.now().plusDays(3),
				weatherStatsResponse.getWeatherStats().get(0).getDate());
		Assert.assertEquals("Today + 2nd data must be present", LocalDate.now().plusDays(2),
				weatherStatsResponse.getWeatherStats().get(1).getDate());
		Assert.assertEquals("Today + 1 data must be present", LocalDate.now().plusDays(1),
				weatherStatsResponse.getWeatherStats().get(2).getDate());

		Assert.assertEquals("Today + 1 avg morning temp not matching", 3.0,
				weatherStatsResponse.getWeatherStats().get(2).getDayAvgTemp(), 0);

		Assert.assertEquals("Today + 1 avg evening temp not matching", 4.0,
				weatherStatsResponse.getWeatherStats().get(2).getNightAvgTemp(), 0);

		Assert.assertEquals("Today + 1 avg pressure not matching", 2.0,
				weatherStatsResponse.getWeatherStats().get(2).getAvgPressure(), 0);
	}

	private OpenWeatherServiceResponse sampleValidResponseWithPastData() {
		OpenWeatherServiceResponse openWeatherServiceResponse = new OpenWeatherServiceResponse();
		openWeatherServiceResponse.setList(Arrays.asList(getWeatherDetail(LocalDateTime.now().minusDays(1))));
		return openWeatherServiceResponse;
	}

	private OpenWeatherServiceResponse sampleValidResponseForFuture() {
		OpenWeatherServiceResponse openWeatherServiceResponse = new OpenWeatherServiceResponse();
		openWeatherServiceResponse.setList(getTestWeatherDetailsForFuture());
		return openWeatherServiceResponse;
	}

	private WeatherDetail getWeatherDetail(LocalDateTime date) {
		WeatherDetail weatherDetail = new WeatherDetail();
		weatherDetail.setDtTxt(formatter.format(date));
		return weatherDetail;
	}

	private WeatherDetail getWeatherDetail(LocalDateTime date, double minTemp, double maxTemp, double pressure) {
		WeatherDetail weatherDetail = new WeatherDetail();
		weatherDetail.setDtTxt(formatter.format(date));
		weatherDetail.setMain(new Main());
		weatherDetail.getMain().setTempMin(minTemp);
		weatherDetail.getMain().setTempMax(maxTemp);
		weatherDetail.getMain().setPressure(pressure);
		return weatherDetail;
	}

	@SuppressWarnings("serial")
	private List<WeatherDetail> getTestWeatherDetailsForFuture() {
		LocalDateTime now = LocalDateTime.now();
		return new ArrayList<WeatherDetail>() {
			{
				// Tomorrow morning data
				this.add(getWeatherDetail(now.plusDays(1).withHour(6), 1, 3, 1));
				this.add(getWeatherDetail(now.plusDays(1).withHour(9), 4, 6, 2));
				this.add(getWeatherDetail(now.plusDays(1).withHour(12), 1, 3, 3));

				// Tomorrow evening data
				this.add(getWeatherDetail(now.plusDays(1).withHour(18), 2, 2, 1));
				this.add(getWeatherDetail(now.plusDays(1).withHour(21), 4, 4, 2));
				this.add(getWeatherDetail(now.plusDays(2).withHour(00), 6, 6, 3));

				// // Tomorrow + 1 morning data
				this.add(getWeatherDetail(now.plusDays(2).withHour(6), 1, 2, 3));
				this.add(getWeatherDetail(now.plusDays(2).withHour(9), 4, 5, 6));
				this.add(getWeatherDetail(now.plusDays(2).withHour(12), 5, 6, 7));

				// Tomorrow + 1 evening data
				this.add(getWeatherDetail(now.plusDays(2).withHour(18), 2, 2, 3));
				this.add(getWeatherDetail(now.plusDays(2).withHour(21), 4, 5, 6));
				this.add(getWeatherDetail(now.plusDays(3).withHour(00), 6, 6, 7));

				// Tomorrow + 2 morning data
				this.add(getWeatherDetail(now.plusDays(3).withHour(6), 1, 2, 3));
				this.add(getWeatherDetail(now.plusDays(3).withHour(9), 4, 5, 6));
				this.add(getWeatherDetail(now.plusDays(3).withHour(12), 5, 6, 7));

				// Tomorrow + 2 evening data
				this.add(getWeatherDetail(now.plusDays(3).withHour(18), 2, 2, 3));
				this.add(getWeatherDetail(now.plusDays(3).withHour(21), 4, 5, 6));
				this.add(getWeatherDetail(now.plusDays(4).withHour(00), 6, 6, 7));

				// Tomorrow + 3 morning data
				this.add(getWeatherDetail(now.plusDays(4).withHour(6), 1, 2, 3));
				this.add(getWeatherDetail(now.plusDays(4).withHour(9), 4, 5, 6));
				this.add(getWeatherDetail(now.plusDays(4).withHour(12), 5, 6, 7));

				// Tomorrow + 3 evening data
				this.add(getWeatherDetail(now.plusDays(4).withHour(18), 2, 2, 3));
				this.add(getWeatherDetail(now.plusDays(4).withHour(21), 4, 5, 6));
				this.add(getWeatherDetail(now.plusDays(5).withHour(00), 6, 6, 7));
			}
		};

	}

}
