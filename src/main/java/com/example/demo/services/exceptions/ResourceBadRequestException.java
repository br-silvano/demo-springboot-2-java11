package com.example.demo.services.exceptions;

public class ResourceBadRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceBadRequestException() {
		super("Received invalid input parameters");
	}
	
	public ResourceBadRequestException(String msg) {
		super(msg);
	}
	
}
