package com.enterprise.talent_hub.api.dto;

import java.time.OffsetDateTime;

public record AuthInviteDetailsDto(
	String companyName,
	String companySlug,
	String email,
	String fullName,
	String employeeRole,
	String membershipRole,
	OffsetDateTime expiresAt,
	boolean existingUser
) {
}
