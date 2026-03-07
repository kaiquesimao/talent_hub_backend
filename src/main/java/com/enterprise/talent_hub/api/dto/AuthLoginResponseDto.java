package com.enterprise.talent_hub.api.dto;

import java.util.List;

public record AuthLoginResponseDto(
	String accessToken,
	String tokenType,
	long expiresIn,
	Long activeCompanyId,
	String activeCompanyName,
	String activeCompanySlug,
	String membershipRole,
	String userName,
	String userEmail,
	List<String> permissions,
	List<AuthCompanyContextDto> companies
) {
}
