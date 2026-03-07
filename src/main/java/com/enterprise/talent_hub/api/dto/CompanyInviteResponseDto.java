package com.enterprise.talent_hub.api.dto;

import java.time.OffsetDateTime;

public record CompanyInviteResponseDto(
	Long id,
	String email,
	String fullName,
	String employeeRole,
	Long countryId,
	String countryName,
	String membershipRole,
	OffsetDateTime expiresAt,
	OffsetDateTime createdAt,
	String inviteToken,
	String acceptUrl
) {
}
