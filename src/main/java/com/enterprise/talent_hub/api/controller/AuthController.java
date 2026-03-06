package com.enterprise.talent_hub.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enterprise.talent_hub.api.dto.AuthLoginRequestDto;
import com.enterprise.talent_hub.api.dto.AuthLoginResponseDto;
import com.enterprise.talent_hub.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public AuthLoginResponseDto login(@Valid @RequestBody AuthLoginRequestDto request) {
		return authService.login(request);
	}
}
