package com.enterprise.talent_hub.api.dto;

import com.enterprise.talent_hub.domain.MembershipRole;
import com.enterprise.talent_hub.domain.MembershipStatus;

import jakarta.validation.constraints.NotNull;

public record CompanyMembershipUpdateRequestDto(
	@NotNull MembershipRole membershipRole,
	@NotNull MembershipStatus membershipStatus
) {
}
