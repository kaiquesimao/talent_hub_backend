package com.enterprise.talent_hub.api.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record EmployeeProfileDto(
	Long id,
	String name,
	String email,
	String role,
	OffsetDateTime createdAt,
	CountryDto country,
	List<EmployeeSkillDetailDto> skills
) {
}
