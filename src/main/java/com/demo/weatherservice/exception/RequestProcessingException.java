package com.demo.weatherservice.exception;

public class RequestProcessingException extends RuntimeException {

	private static final String DEFAULT_ERROR_MESSAGE = "Failed to execute request due to server error.";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequestProcessingException() {
		this(DEFAULT_ERROR_MESSAGE);
	}

	public RequestProcessingException(String msg) {
		super(msg);
	}

	public RequestProcessingException(Throwable cause) {
		super(DEFAULT_ERROR_MESSAGE, cause);
	}

	public RequestProcessingException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
