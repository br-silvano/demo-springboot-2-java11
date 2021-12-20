package com.example.demo.dto;

import java.io.Serializable;

public class TokenRefreshRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String refreshToken;

	public TokenRefreshRequest() {
	}

	public TokenRefreshRequest(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

}
