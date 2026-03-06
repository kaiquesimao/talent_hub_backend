package com.enterprise.talent_hub.api.dto;

import com.enterprise.talent_hub.domain.SkillCategory;

public record SkillDto(
	Long id,
	String name,
	SkillCategory category
) {
}
