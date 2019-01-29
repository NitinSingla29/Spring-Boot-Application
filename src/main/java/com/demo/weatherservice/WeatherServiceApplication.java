package com.demo.weatherservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class WeatherServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(WeatherServiceApplication.class, args);
	}
}

