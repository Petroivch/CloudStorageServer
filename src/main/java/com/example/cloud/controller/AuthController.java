package com.example.cloud.controller;

import javax.validation.Valid;

import com.example.cloud.jwt.JwtTokenUtil;
import com.example.cloud.dto.AuthRequest;
import com.example.cloud.dto.AuthResponse;
import com.example.cloud.repository.CloudRepository;
import com.example.cloud.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.cloud.entity.User;

@RestController
public class AuthController {
	private CloudRepository cloudRepository;
	@Autowired AuthenticationManager authManager;
	@Autowired
    JwtTokenUtil jwtUtil;
	public AuthController(CloudRepository cloudRepository) {
		this.cloudRepository = cloudRepository;
	}
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
		try {
			Authentication authentication = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getEmail(), request.getPassword())
			);
			
			User user = (User) authentication.getPrincipal();
			String accessToken = jwtUtil.generateAccessToken(user);
			AuthResponse response = new AuthResponse(user.getEmail(), accessToken);
			cloudRepository.login(accessToken, user);
			return ResponseEntity.ok().body(response);
			
		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	@PostMapping("/logout")
	public HttpStatus logout(@RequestHeader("auth-token") String authToken) {
		cloudRepository.logout(authToken).orElseThrow(() -> new Error("No valid logout"));
		return HttpStatus.OK;
	}


}
