package com.enterprise.talent_hub.api.dto;

import com.enterprise.talent_hub.domain.ProficiencyLevel;

public record EmployeeSkillSummaryDto(
	Long skillId,
	String skillName,
	ProficiencyLevel proficiencyLevel
) {
}
