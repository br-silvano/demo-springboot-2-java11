package com.example.demo;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;

public class TestRestClient {

	private TestRestTemplate rest;

	public TestRestClient(TestRestTemplate testRestTemplate) {
		this.rest = testRestTemplate;
	}

	private HttpHeaders headers(JwtResponse credentials) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + credentials.getAccessToken());
		return headers;
	}

	public <T> ResponseEntity<T> get(String restPath, JwtResponse credentials) {
		return rest.exchange(restPath, HttpMethod.GET, new HttpEntity<>(null, headers(credentials)),
				new ParameterizedTypeReference<T>() {
				});
	}

	public <T> ResponseEntity<T> get(String restPath, JwtResponse credentials, Class<T> responseType) {
		return rest.exchange(restPath, HttpMethod.GET, new HttpEntity<>(null, headers(credentials)), responseType);
	}

	public <T> ResponseEntity<T> post(String restPath, JwtResponse credentials, Object body,
			Class<T> responseType) {
		return rest.exchange(restPath, HttpMethod.POST, new HttpEntity<>(body, headers(credentials)), responseType);
	}

	public <T> ResponseEntity<T> put(String restPath, JwtResponse credentials, Object body,
			Class<T> responseType) {
		return rest.exchange(restPath, HttpMethod.PUT, new HttpEntity<>(body, headers(credentials)), responseType);
	}

	public <T> ResponseEntity<T> delete(String restPath, JwtResponse credentials, Object body) {
		return rest.exchange(restPath, HttpMethod.DELETE, new HttpEntity<>(body, headers(credentials)),
				new ParameterizedTypeReference<T>() {
				});
	}

	public JwtResponse login(String restPath, String username, String password) {
		LoginRequest login = new LoginRequest(username, password);

		ResponseEntity<JwtResponse> response = rest.exchange(restPath, HttpMethod.POST,
				new HttpEntity<LoginRequest>(login), new ParameterizedTypeReference<JwtResponse>() {
				});

		JwtResponse authenticationResponse = response.getBody();

		return authenticationResponse;
	}

}
