package com.enterprise.talent_hub.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthLoginRequestDto(
	@NotBlank @Email String email,
	@NotBlank @Size(min = 3, max = 120) String password
) {
}
