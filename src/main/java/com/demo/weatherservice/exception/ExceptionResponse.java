package com.demo.weatherservice.exception;

import java.time.LocalDateTime;

public class ExceptionResponse {

	private int code;
	private String message;
	private LocalDateTime timeStamp;
	private String details;

	public ExceptionResponse(int code, String message, LocalDateTime timeStamp, String details) {
		super();
		this.code = code;
		this.message = message;
		this.timeStamp = timeStamp;
		this.details = details;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public String getDetails() {
		return details;
	}

}
