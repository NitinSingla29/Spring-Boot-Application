package com.demo.weatherservice.dto;

public class WeatherRequest {

  private String city;

  public WeatherRequest() {
  }

  public WeatherRequest(String city) {
    this.city = city;

  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }
}
