package com.enterprise.talent_hub.api.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.enterprise.talent_hub.api.dto.EmployeeProfileDto;
import com.enterprise.talent_hub.api.dto.EmployeeSearchResponseDto;
import com.enterprise.talent_hub.api.dto.EmployeeSkillsUpdateRequestDto;
import com.enterprise.talent_hub.domain.ProficiencyLevel;
import com.enterprise.talent_hub.service.TalentSearchService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeController {

	private final TalentSearchService talentSearchService;

	@GetMapping
	public EmployeeSearchResponseDto search(
		@RequestParam(value = "q", required = false) String query,
		@RequestParam(required = false) Long countryId,
		@RequestParam(required = false) Long skillId,
		@RequestParam(required = false) ProficiencyLevel proficiencyLevel,
		@RequestParam(defaultValue = "0") @Min(0) int page,
		@RequestParam(defaultValue = "12") @Min(1) @Max(100) int size
	) {
		return talentSearchService.searchEmployees(query, countryId, skillId, proficiencyLevel, page, size);
	}

	@GetMapping("/me")
	public EmployeeProfileDto me() {
		return talentSearchService.findCurrentEmployeeProfile();
	}

	@GetMapping("/{id}")
	public EmployeeProfileDto findById(@PathVariable @Positive Long id) {
		return talentSearchService.findEmployeeProfile(id);
	}

	@PutMapping("/{id}/skills")
	public EmployeeProfileDto updateSkills(
			@PathVariable @Positive Long id,
			@Valid @RequestBody EmployeeSkillsUpdateRequestDto request) {
		return talentSearchService.updateEmployeeSkills(id, request);
	}
}
