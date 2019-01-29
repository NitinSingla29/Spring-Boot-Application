package com.demo.weatherservice.service.openweather;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.demo.weatherservice.dto.WeatherStats;
import com.demo.weatherservice.dto.WeatherStatsResponse;
import com.demo.weatherservice.service.openweather.model.WeatherDetail;
import com.demo.weatherservice.utility.DateUtil;

@Component
public class OpenWeatherResponseTransformer implements Function<OpenWeatherServiceResponse, WeatherStatsResponse> {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public WeatherStatsResponse apply(OpenWeatherServiceResponse openWeatherServiceResponse) {
		LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(3).withMinute(0).withSecond(0).withNano(0);
		LocalDateTime endTime = startTime.plusDays(3).withHour(6);

		// Filter all weather details which are not for next 3 days
		Stream<WeatherDetail> filterWheatherDetails = openWeatherServiceResponse.getList().stream()
				.filter((w) -> (weatherTimeInRange(w.getDtTxt(), startTime, endTime)));

		// Convert the weather details to intermediate statistics in which we
		// are interested
		Stream<IntermediatesStats> filteredIntermediatesStatsStream = filterWheatherDetails
				.map(wd -> new IntermediatesStats(wd));

		// Group the weather statistics based on date
		Map<LocalDate, List<IntermediatesStats>> intermediateStatsMap = new TreeMap<>(
				filteredIntermediatesStatsStream.collect(Collectors.groupingBy(ws -> ws.date)));

		Map<LocalDate, WeatherStats> weatherStatsMap = intermediateStatsMap.entrySet().stream().collect(
				Collectors.toMap(Entry::getKey, e -> this.transform(sumIntermediatesStats(e.getValue()), e.getKey())));

		return new WeatherStatsResponse(weatherStatsMap.values().stream().collect(Collectors.toList()));
	}

	/**
	 * Calculate the Sum of day temperature, night temperate and pressure for
	 * the given weather statistics
	 * 
	 * @param intermediatesStatsList
	 * @return
	 */
	private IntermediatesStats sumIntermediatesStats(List<IntermediatesStats> intermediatesStatsList) {
		return intermediatesStatsList.stream().reduce(new IntermediatesStats(), this::add);
	}

	/**
	 * Add the given weather statistics
	 * 
	 * @param stas1
	 * @param stas2
	 * @return
	 */
	private IntermediatesStats add(IntermediatesStats stas1, IntermediatesStats stas2) {

		if (stas2 != null) {
			stas1.totalDayTemp = stas1.totalDayTemp + stas2.totalDayTemp;
			stas1.totalNightTemp = stas1.totalNightTemp + stas2.totalNightTemp;
			stas1.totalPressure = stas1.totalPressure + stas2.totalPressure;
			stas1.morningEntriesCount = stas1.morningEntriesCount + stas2.morningEntriesCount;
			stas1.nightEntriesCount = stas1.nightEntriesCount + stas2.nightEntriesCount;
		}
		return stas1;
	}

	/**
	 * Convert the summed weather statistics to averaged weather statistics
	 * 
	 * @param intermediatesStats
	 * @param weatherDate
	 * @return
	 */
	private WeatherStats transform(IntermediatesStats intermediatesStats, LocalDate weatherDate) {
		int totalEntriesCount = intermediatesStats.morningEntriesCount + intermediatesStats.nightEntriesCount;
		WeatherStats result = new WeatherStats();
		result.setAvgPressure(intermediatesStats.totalPressure / totalEntriesCount);
		result.setDayAvgTemp(intermediatesStats.totalDayTemp / intermediatesStats.morningEntriesCount);
		result.setNightAvgTemp(intermediatesStats.totalNightTemp / intermediatesStats.nightEntriesCount);
		result.setDate(weatherDate);
		return result;
	}

	private boolean weatherTimeInRange(String dateStr, LocalDateTime startTime, LocalDateTime endTime) {
		LocalDateTime weatherTime = DateUtil.toLocalDateTime(dateStr, DATE_FORMAT);
		return weatherTime.isAfter(startTime) && weatherTime.isBefore(endTime);
	}

	private class IntermediatesStats {

		double totalPressure;
		double totalDayTemp;
		double totalNightTemp;
		int morningEntriesCount;
		int nightEntriesCount;
		LocalDateTime dateTime;
		LocalDate date;

		IntermediatesStats() {
		}

		IntermediatesStats(WeatherDetail wd) {
			this.totalPressure = wd.getMain().getPressure();
			LocalDateTime weatherTime = DateUtil.toLocalDateTime(wd.getDtTxt(), DATE_FORMAT);
			this.dateTime = weatherTime;
			this.date = getWeatherDateKey(weatherTime);
			if (isMorningTime(weatherTime)) {
				this.totalDayTemp = getTimeSlotAverageTemp(wd);
				this.morningEntriesCount = this.morningEntriesCount + 1;
			} else {
				this.totalNightTemp = getTimeSlotAverageTemp(wd);
				this.nightEntriesCount = this.nightEntriesCount + 1;
			}
		}

		private boolean isMorningTime(LocalDateTime weatherTime) {
			int hour = weatherTime.getHour();
			if (hour >= 6 && hour < 18) {
				return true;
			}
			return false;
		}

		private Double getTimeSlotAverageTemp(WeatherDetail weatherDetail) {
			return (weatherDetail.getMain().getTempMin() + weatherDetail.getMain().getTempMax()) / 2;
		}

		private LocalDate getWeatherDateKey(LocalDateTime weatherTime) {
			int hour = weatherTime.getHour();
			if (hour >= 0 && hour <= 3) {
				return weatherTime.minusDays(1).toLocalDate();
			}
			return weatherTime.toLocalDate();
		}

		@Override
		public String toString() {
			return "IntermediatesStats [totalPressure=" + totalPressure + ", totalDayTemp=" + totalDayTemp
					+ ", totalNightTemp=" + totalNightTemp + ", morningEntriesCount=" + morningEntriesCount
					+ ", nightEntriesCount=" + nightEntriesCount + ", dateTime=" + dateTime + ", date=" + date + "] \n";
		}

	}
}
