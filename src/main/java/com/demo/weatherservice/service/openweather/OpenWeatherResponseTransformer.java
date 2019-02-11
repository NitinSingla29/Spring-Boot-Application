package com.demo.weatherservice.service.openweather;

import com.demo.weatherservice.dto.WeatherStats;
import com.demo.weatherservice.dto.WeatherStatsResponse;
import com.demo.weatherservice.service.openweather.model.WeatherDetail;
import com.demo.weatherservice.utility.DateUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

@Component
public class OpenWeatherResponseTransformer implements Function<OpenWeatherServiceResponse, WeatherStatsResponse> {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public WeatherStatsResponse apply(OpenWeatherServiceResponse openWeatherServiceResponse) {
    LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(3).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime endTime = startTime.plusDays(3).withHour(6);

    // Filter all weather details which are not for next 3 days
    Stream<WeatherDetail> filterWheatherDetails = openWeatherServiceResponse.getList().stream().parallel().filter((w) -> (weatherTimeInRange(w.getDtTxt(),
            startTime, endTime)));

    // Convert the weather details to intermediate statistics in which we are interested
    Stream<IntermediatesStats> filteredIntermediatesStatsStream = filterWheatherDetails.map(IntermediatesStats::new);

    // Group the weather statistics based on date
    Map<LocalDate, List<IntermediatesStats>> intermediateStatsMap = new TreeMap<>(filteredIntermediatesStatsStream.collect(Collectors.groupingBy(ws
            -> ws.date)));

    Map<LocalDate, WeatherStats> weatherStatsMap = intermediateStatsMap.entrySet().stream().parallel().collect(toMap(Entry::getKey, e -> e.getValue().stream()
            .collect(collectingAndThen(reducing(new IntermediatesStats(), this::add), this::transform))));

    return new WeatherStatsResponse(new ArrayList<>(weatherStatsMap.values()));
  }

  public WeatherStatsResponse apply1(OpenWeatherServiceResponse openWeatherServiceResponse) {
    LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(3).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime endTime = startTime.plusDays(3).withHour(6);

    // Filter all weather details which are not for next 3 days
    Stream<WeatherDetail> filterWheatherDetails = openWeatherServiceResponse.getList().stream().filter((w) -> (weatherTimeInRange(w.getDtTxt(),
            startTime, endTime)));

    // Convert the weather details to intermediate statistics in which we are interested
    Stream<IntermediatesStats> filteredIntermediatesStatsStream = filterWheatherDetails.map(IntermediatesStats::new);

    Map<LocalDate, WeatherStats> weatherStatsMap = filteredIntermediatesStatsStream.collect(groupingBy(is -> is.date, Collector.of
            (IntermediatesStats::new, this::add, this::add, this::transform)));
    return new WeatherStatsResponse(new ArrayList<>(weatherStatsMap.values()));
  }


  public WeatherStatsResponse apply_org(OpenWeatherServiceResponse openWeatherServiceResponse) {
    LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(3).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime endTime = startTime.plusDays(3).withHour(6);

    // Filter all weather details which are not for next 3 days
    Stream<WeatherDetail> filterWheatherDetails = openWeatherServiceResponse.getList().stream().filter((w) -> (weatherTimeInRange(w.getDtTxt(),
            startTime, endTime)));

    // Convert the weather details to intermediate statistics in which we are interested
    Stream<IntermediatesStats> filteredIntermediatesStatsStream = filterWheatherDetails.map(IntermediatesStats::new);

    // Group the weather statistics based on date
    Map<LocalDate, List<IntermediatesStats>> intermediateStatsMap = new TreeMap<>(filteredIntermediatesStatsStream.collect(Collectors.groupingBy(ws
            -> ws.date)));

    Map<LocalDate, WeatherStats> weatherStatsMap = intermediateStatsMap.entrySet().stream().collect(toMap(Entry::getKey, e -> this.transform
            (sumIntermediatesStats(e.getValue()))));


    return new WeatherStatsResponse(new ArrayList<>(weatherStatsMap.values()));
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
    IntermediatesStats result = stas1;

    result.totalDayTemp = stas1.totalDayTemp + stas2.totalDayTemp;
    result.totalNightTemp = stas1.totalNightTemp + stas2.totalNightTemp;
    result.totalPressure = stas1.totalPressure + stas2.totalPressure;
    result.morningEntriesCount = stas1.morningEntriesCount + stas2.morningEntriesCount;
    result.nightEntriesCount = stas1.nightEntriesCount + stas2.nightEntriesCount;
    result.date = stas2.date;
    return result;
  }

  /**
   * Convert the summed weather statistics to averaged weather statistics
   *
   * @param intermediatesStats
   * @return
   */
  private WeatherStats transform(IntermediatesStats intermediatesStats) {
    int totalEntriesCount = intermediatesStats.morningEntriesCount + intermediatesStats.nightEntriesCount;
    WeatherStats result = new WeatherStats();
    result.setAvgPressure(intermediatesStats.totalPressure / totalEntriesCount);
    result.setDayAvgTemp(intermediatesStats.totalDayTemp / intermediatesStats.morningEntriesCount);
    result.setNightAvgTemp(intermediatesStats.totalNightTemp / intermediatesStats.nightEntriesCount);
    result.setDate(intermediatesStats.date);
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
      System.out.println("Intermediates stats created");

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
      return "IntermediatesStats [totalPressure=" + totalPressure + ", totalDayTemp=" + totalDayTemp + ", totalNightTemp=" + totalNightTemp + ", "
              + "morningEntriesCount=" + morningEntriesCount + ", nightEntriesCount=" + nightEntriesCount + ", dateTime=" + dateTime + ", date=" +
              date + "] \n";
    }

  }
}
