package com.enterprise.talent_hub.api.dto;

import com.enterprise.talent_hub.domain.ProficiencyLevel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EmployeeSkillUpdateItemDto(
	@NotNull @Positive Long skillId,
	@NotNull ProficiencyLevel proficiencyLevel,
	@NotNull @Min(0) @Max(60) Integer yearsExperience
) {
}
