package com.enterprise.talent_hub.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enterprise.talent_hub.api.dto.CountryDto;
import com.enterprise.talent_hub.api.dto.EmployeeProfileDto;
import com.enterprise.talent_hub.api.dto.EmployeeSearchResponseDto;
import com.enterprise.talent_hub.api.dto.EmployeeSummaryDto;
import com.enterprise.talent_hub.api.dto.SkillDto;
import com.enterprise.talent_hub.api.mapper.CountryMapper;
import com.enterprise.talent_hub.api.mapper.EmployeeMapper;
import com.enterprise.talent_hub.api.mapper.SkillMapper;
import com.enterprise.talent_hub.domain.Employee;
import com.enterprise.talent_hub.domain.ProficiencyLevel;
import com.enterprise.talent_hub.repository.CountryRepository;
import com.enterprise.talent_hub.repository.EmployeeRepository;
import com.enterprise.talent_hub.repository.SkillRepository;
import com.enterprise.talent_hub.service.exception.ResourceNotFoundException;
import com.enterprise.talent_hub.service.specification.EmployeeSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TalentSearchService {

	private final EmployeeRepository employeeRepository;
	private final CountryRepository countryRepository;
	private final SkillRepository skillRepository;
	private final CountryMapper countryMapper;
	private final SkillMapper skillMapper;
	private final EmployeeMapper employeeMapper;

	public List<CountryDto> listCountries() {
		return countryMapper.toDto(countryRepository.findAllByOrderByNameAsc());
	}

	public List<SkillDto> listSkills() {
		return skillMapper.toDto(skillRepository.findAllByOrderByNameAsc());
	}

	public EmployeeSearchResponseDto searchEmployees(
		String query,
		Long countryId,
		Long skillId,
		ProficiencyLevel proficiencyLevel,
		int page,
		int size
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
		Specification<Employee> specification = EmployeeSpecifications.withFilters(query, countryId, skillId, proficiencyLevel);
		Page<Employee> resultPage = employeeRepository.findAll(specification, pageable);

		List<EmployeeSummaryDto> summaries = resultPage.stream()
			.map(employeeMapper::toSummary)
			.toList();

		return new EmployeeSearchResponseDto(
			summaries,
			resultPage.getNumber(),
			resultPage.getSize(),
			resultPage.getTotalElements(),
			resultPage.getTotalPages(),
			resultPage.hasNext(),
			resultPage.hasPrevious()
		);
	}

	public EmployeeProfileDto findEmployeeProfile(Long id) {
		Employee employee = employeeRepository.findDetailedById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Employee with id %d was not found".formatted(id)));

		return employeeMapper.toProfile(employee);
	}
}
