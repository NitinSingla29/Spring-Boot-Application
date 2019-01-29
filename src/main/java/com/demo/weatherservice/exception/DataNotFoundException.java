package com.demo.weatherservice.exception;

public class DataNotFoundException extends RuntimeException {

	private static final String DEFAULT_ERROR_MESSAGE = "Failed to find requested data.";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataNotFoundException() {
		this(DEFAULT_ERROR_MESSAGE);
	}

	public DataNotFoundException(String msg) {
		super(msg);
	}

	public DataNotFoundException(Throwable cause) {
		super(DEFAULT_ERROR_MESSAGE, cause);
	}

	public DataNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
