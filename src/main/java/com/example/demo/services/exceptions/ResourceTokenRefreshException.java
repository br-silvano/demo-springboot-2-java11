package com.example.demo.services.exceptions;

public class ResourceTokenRefreshException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceTokenRefreshException(String token, String message) {
		super(String.format("Failed for [%s]: %s", token, message));
	}

}
