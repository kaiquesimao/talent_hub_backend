package com.enterprise.talent_hub.api.dto;

import com.enterprise.talent_hub.domain.MembershipRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CompanyInviteRequestDto(
	@NotBlank @Email String email,
	@NotBlank @Size(min = 2, max = 180) String fullName,
	@NotBlank @Size(min = 2, max = 120) String employeeRole,
	@NotNull @Positive Long countryId,
	@NotNull MembershipRole membershipRole
) {
}
