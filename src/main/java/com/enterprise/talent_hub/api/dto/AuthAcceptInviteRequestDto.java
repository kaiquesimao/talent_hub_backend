package com.enterprise.talent_hub.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthAcceptInviteRequestDto(
	@NotBlank String token,
	@NotBlank @Size(min = 8, max = 120) String password
) {
}
