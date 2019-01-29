package com.demo.weatherservice.dto;

import java.time.LocalDate;

public class WeatherStats {

  private double dayAvgTemp;

  private double nightAvgTemp;

  private double avgPressure;
  
  private LocalDate date;

  public WeatherStats() {
  }

  public WeatherStats(double dayAvgTemp, double nightAvgTemp, double avgPressure, LocalDate date) {

    this.dayAvgTemp = dayAvgTemp;
    this.nightAvgTemp = nightAvgTemp;
    this.avgPressure = avgPressure;
	this.setDate(date);
  }

  public double getDayAvgTemp() {
    return dayAvgTemp;

  }

  public double getNightAvgTemp() {
    return nightAvgTemp;
  }

  public void setNightAvgTemp(double nightAvgTemp) {
    this.nightAvgTemp = nightAvgTemp;
  }

  public double getAvgPressure() {
    return avgPressure;
  }

  public void setAvgPressure(double avgPressure) {
    this.avgPressure = avgPressure;
  }

  public void setDayAvgTemp(double dayAvgTemp) {
    this.dayAvgTemp = dayAvgTemp;
  }

public LocalDate getDate() {
	return date;
}

public void setDate(LocalDate date) {
	this.date = date;
}
}
