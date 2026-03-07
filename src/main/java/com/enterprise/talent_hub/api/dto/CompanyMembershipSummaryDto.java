package com.enterprise.talent_hub.api.dto;

import java.time.OffsetDateTime;

public record CompanyMembershipSummaryDto(
	Long membershipId,
	Long userId,
	Long employeeId,
	String fullName,
	String email,
	String employeeRole,
	String membershipRole,
	String membershipStatus,
	boolean isDefault,
	OffsetDateTime joinedAt
) {
}
