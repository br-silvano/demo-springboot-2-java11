package com.example.demo.dto;

import java.io.Serializable;
import java.util.List;

public class JwtResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accessToken;
	private String tokenType = "Bearer";
	private String refreshToken;
	private Long id;
	private String name;
	private List<String> roles;

	public JwtResponse() {
	}

	public JwtResponse(String accessToken, String refreshToken, Long id, String name, List<String> roles) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.id = id;
		this.name = name;
		this.roles = roles;
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
	
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<String> getRoles() {
		return roles;
	}

}
