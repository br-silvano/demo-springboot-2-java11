package com.example.demo.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.TokenRefreshRequest;
import com.example.demo.dto.TokenRefreshResponse;
import com.example.demo.entities.RefreshToken;
import com.example.demo.security.JwtTokenUtil;
import com.example.demo.security.impl.UserDetailsImpl;
import com.example.demo.services.exceptions.ResourceBadRequestException;
import com.example.demo.services.exceptions.ResourceTokenRefreshException;

@Service
public class AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private RefreshTokenService refreshTokenService;

	public JwtResponse autenticate(LoginRequest authenticationRequest) {
		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (DisabledException e) {
			throw new ResourceBadRequestException(e.getMessage());
		} catch (BadCredentialsException e) {
			throw new ResourceBadRequestException(e.getMessage());
		}

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		final String jwt = jwtTokenUtil.generateToken(userDetails);

		final List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		final RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

		return new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(), userDetails.getName(), roles);
	}

	public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
		String requestRefreshToken = request.getRefreshToken();

		return refreshTokenService.findByToken(requestRefreshToken).map(refreshTokenService::verifyExpiration)
				.map(RefreshToken::getUser).map(user -> {
					String token = jwtTokenUtil.generateTokenFromUsername(user.getEmail());
					return new TokenRefreshResponse(token, requestRefreshToken);
				}).orElseThrow(() -> new ResourceTokenRefreshException(requestRefreshToken,
						"Refresh token is not in database!"));
	}

}
