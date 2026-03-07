package com.enterprise.talent_hub.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AuthSwitchCompanyRequestDto(
	@NotNull @Positive Long companyId
) {
}
