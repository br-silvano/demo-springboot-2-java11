package com.example.demo.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.TokenRefreshRequest;
import com.example.demo.dto.TokenRefreshResponse;
import com.example.demo.services.AuthService;

@RestController
@RequestMapping(value = "/auth")
public class AuthResource {

	@Autowired
	private AuthService service;

	@PostMapping("/token")
	public ResponseEntity<JwtResponse> authenticate(
			@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok().body(service.autenticate(loginRequest));
	}
	
	@PostMapping("/refreshtoken")
	public ResponseEntity<TokenRefreshResponse> refreshtoken(
			@RequestBody TokenRefreshRequest tokenRefreshRequest) {
		return ResponseEntity.ok().body(service.refreshToken(tokenRefreshRequest));
	}

}
