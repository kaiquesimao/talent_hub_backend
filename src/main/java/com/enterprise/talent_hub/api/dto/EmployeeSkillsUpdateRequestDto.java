package com.enterprise.talent_hub.api.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record EmployeeSkillsUpdateRequestDto(
	@NotNull List<@Valid EmployeeSkillUpdateItemDto> skills
) {
}
