package com.enterprise.talent_hub.api.mapper;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.enterprise.talent_hub.api.dto.EmployeeProfileDto;
import com.enterprise.talent_hub.api.dto.EmployeeSkillDetailDto;
import com.enterprise.talent_hub.api.dto.EmployeeSummaryDto;
import com.enterprise.talent_hub.domain.Employee;
import com.enterprise.talent_hub.domain.EmployeeSkill;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = CountryMapper.class)
public interface EmployeeMapper {

	@Mapping(target = "countryName", source = "country.name")
	@Mapping(target = "countryIsoCode", source = "country.isoCode")
	EmployeeSummaryDto toSummary(Employee employee);

	@Mapping(target = "skillId", source = "skill.id")
	@Mapping(target = "skillName", source = "skill.name")
	@Mapping(target = "category", source = "skill.category")
	EmployeeSkillDetailDto toSkillDetail(EmployeeSkill employeeSkill);

	@Mapping(target = "skills", expression = "java(toSortedSkillDetails(employee.getEmployeeSkills()))")
	EmployeeProfileDto toProfile(Employee employee);

	default List<EmployeeSkillDetailDto> toSortedSkillDetails(Set<EmployeeSkill> employeeSkills) {
		if (employeeSkills == null || employeeSkills.isEmpty()) {
			return List.of();
		}

		return employeeSkills.stream()
				.sorted(Comparator.comparing(skill -> skill.getSkill().getName(), String.CASE_INSENSITIVE_ORDER))
				.map(this::toSkillDetail)
				.toList();
	}
}
