package com.demo.weatherservice.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

	public static LocalDateTime toLocalDateTime(String dateStr, DateTimeFormatter df) {
		return LocalDateTime.parse(dateStr, df).withNano(0);
	}
}
