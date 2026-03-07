package com.enterprise.talent_hub.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enterprise.talent_hub.api.dto.AuthAcceptInviteRequestDto;
import com.enterprise.talent_hub.api.dto.AuthCompanyContextDto;
import com.enterprise.talent_hub.api.dto.AuthInviteDetailsDto;
import com.enterprise.talent_hub.api.dto.AuthLoginRequestDto;
import com.enterprise.talent_hub.api.dto.AuthLoginResponseDto;
import com.enterprise.talent_hub.api.dto.AuthRegisterCompanyRequestDto;
import com.enterprise.talent_hub.api.dto.AuthSwitchCompanyRequestDto;
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

	@PostMapping("/register-company")
	public AuthLoginResponseDto registerCompany(@Valid @RequestBody AuthRegisterCompanyRequestDto request) {
		return authService.registerCompany(request);
	}

	@GetMapping("/companies")
	public List<AuthCompanyContextDto> companies() {
		return authService.listCompanies();
	}

	@PostMapping("/switch-company")
	public AuthLoginResponseDto switchCompany(@Valid @RequestBody AuthSwitchCompanyRequestDto request) {
		return authService.switchCompany(request);
	}

	@GetMapping("/invites/{token}")
	public AuthInviteDetailsDto inviteDetails(@PathVariable String token) {
		return authService.getInviteDetails(token);
	}

	@PostMapping("/accept-invite")
	public AuthLoginResponseDto acceptInvite(@Valid @RequestBody AuthAcceptInviteRequestDto request) {
		return authService.acceptInvite(request);
	}
}
