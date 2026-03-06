package com.enterprise.talent_hub.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enterprise.talent_hub.api.dto.CountryDto;
import com.enterprise.talent_hub.api.dto.SkillDto;
import com.enterprise.talent_hub.service.TalentSearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meta")
public class MetadataController {

	private final TalentSearchService talentSearchService;

	@GetMapping("/countries")
	public List<CountryDto> countries() {
		return talentSearchService.listCountries();
	}

	@GetMapping("/skills")
	public List<SkillDto> skills() {
		return talentSearchService.listSkills();
	}
}
