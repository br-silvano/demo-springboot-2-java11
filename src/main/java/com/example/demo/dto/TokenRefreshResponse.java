package com.example.demo.dto;

import java.io.Serializable;

public class TokenRefreshResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String accessToken;
	private String tokenType = "Bearer";
	private String refreshToken;

	public TokenRefreshResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
}
