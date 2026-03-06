package com.enterprise.talent_hub.api.dto;

import com.enterprise.talent_hub.domain.ProficiencyLevel;
import com.enterprise.talent_hub.domain.SkillCategory;

public record EmployeeSkillDetailDto(
	Long skillId,
	String skillName,
	SkillCategory category,
	ProficiencyLevel proficiencyLevel,
	Integer yearsExperience,
	String validatedBy
) {
}
