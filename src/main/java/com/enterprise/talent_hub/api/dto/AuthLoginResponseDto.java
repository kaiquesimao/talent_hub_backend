package com.enterprise.talent_hub.api.dto;

public record AuthLoginResponseDto(
	String accessToken,
	String tokenType,
	long expiresIn,
	String userName,
	String userEmail
) {
}
