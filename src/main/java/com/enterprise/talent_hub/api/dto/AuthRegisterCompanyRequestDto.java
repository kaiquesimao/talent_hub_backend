package com.enterprise.talent_hub.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AuthRegisterCompanyRequestDto(
	@NotBlank @Size(min = 2, max = 180) String companyName,
	@NotBlank @Size(min = 2, max = 180) String companySlug,
	@NotBlank @Size(min = 2, max = 180) String adminName,
	@NotBlank @Email String adminEmail,
	@NotBlank @Size(min = 8, max = 120) String password,
	@NotNull @Positive Long adminCountryId,
	@NotBlank @Size(min = 2, max = 120) String adminRole
) {
}
