package com.example.demo.dto;

import java.io.Serializable;

public class LoginRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	
	public LoginRequest()
	{
	}

	public LoginRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}