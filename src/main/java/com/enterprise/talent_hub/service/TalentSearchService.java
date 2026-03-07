package com.enterprise.talent_hub.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enterprise.talent_hub.api.dto.CountryDto;
import com.enterprise.talent_hub.api.dto.EmployeeProfileDto;
import com.enterprise.talent_hub.api.dto.EmployeeSearchResponseDto;
import com.enterprise.talent_hub.api.dto.EmployeeSkillUpdateItemDto;
import com.enterprise.talent_hub.api.dto.EmployeeSkillsUpdateRequestDto;
import com.enterprise.talent_hub.api.dto.EmployeeSummaryDto;
import com.enterprise.talent_hub.api.dto.SkillDto;
import com.enterprise.talent_hub.api.mapper.CountryMapper;
import com.enterprise.talent_hub.api.mapper.EmployeeMapper;
import com.enterprise.talent_hub.api.mapper.SkillMapper;
import com.enterprise.talent_hub.domain.Employee;
import com.enterprise.talent_hub.domain.EmployeeSkill;
import com.enterprise.talent_hub.domain.EmployeeSkillId;
import com.enterprise.talent_hub.domain.Permission;
import com.enterprise.talent_hub.domain.ProficiencyLevel;
import com.enterprise.talent_hub.domain.Skill;
import com.enterprise.talent_hub.repository.CountryRepository;
import com.enterprise.talent_hub.repository.EmployeeRepository;
import com.enterprise.talent_hub.repository.EmployeeSkillRepository;
import com.enterprise.talent_hub.repository.SkillRepository;
import com.enterprise.talent_hub.service.auth.AuthenticatedCompanyContext;
import com.enterprise.talent_hub.service.auth.CurrentTenantService;
import com.enterprise.talent_hub.service.exception.ConflictException;
import com.enterprise.talent_hub.service.exception.ResourceNotFoundException;
import com.enterprise.talent_hub.service.specification.EmployeeSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TalentSearchService {

	private static final String EMPLOYEE_NOT_FOUND_MESSAGE = "Employee with id %d was not found";

	private final EmployeeRepository employeeRepository;
	private final CountryRepository countryRepository;
	private final SkillRepository skillRepository;
	private final EmployeeSkillRepository employeeSkillRepository;
	private final CountryMapper countryMapper;
	private final SkillMapper skillMapper;
	private final EmployeeMapper employeeMapper;
	private final CurrentTenantService currentTenantService;
	private final EntityManager entityManager;

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
		AuthenticatedCompanyContext context = currentTenantService.requireContext();
		currentTenantService.requirePermission(Permission.EMPLOYEES_READ);
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
		Specification<Employee> specification = EmployeeSpecifications.withFilters(context.companyId(), query,
				countryId, skillId, proficiencyLevel);
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

	public EmployeeProfileDto findCurrentEmployeeProfile() {
		AuthenticatedCompanyContext context = currentTenantService.requireContext();
		currentTenantService.requirePermission(Permission.EMPLOYEES_READ);
		Employee employee = employeeRepository.findDetailedByCompanyIdAndUserId(context.companyId(), context.userId())
				.orElseThrow(
						() -> new ResourceNotFoundException("Employee profile for the current user was not found"));

		return employeeMapper.toProfile(employee);
	}

	public EmployeeProfileDto findEmployeeProfile(Long id) {
		AuthenticatedCompanyContext context = currentTenantService.requireContext();
		currentTenantService.requirePermission(Permission.EMPLOYEES_READ);
		Employee employee = employeeRepository.findDetailedById(id, context.companyId())
				.orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE_NOT_FOUND_MESSAGE.formatted(id)));

		return employeeMapper.toProfile(employee);
	}

	@Transactional
	public EmployeeProfileDto updateEmployeeSkills(Long id, EmployeeSkillsUpdateRequestDto request) {
		AuthenticatedCompanyContext context = currentTenantService.requireContext();
		Employee employee = employeeRepository.findDetailedById(id, context.companyId())
				.orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE_NOT_FOUND_MESSAGE.formatted(id)));
		boolean isSelfProfile = employee.getUser().getId().equals(context.userId());

		if (!isSelfProfile && !context.hasPermission(Permission.EMPLOYEES_UPDATE)) {
			throw new AccessDeniedException("You do not have permission to update this employee's skills");
		}

		Set<Long> distinctSkillIds = new HashSet<>();
		for (EmployeeSkillUpdateItemDto skill : request.skills()) {
			if (!distinctSkillIds.add(skill.skillId())) {
				throw new ConflictException("The same skill cannot be selected more than once");
			}
		}

		Map<Long, Skill> skillsById = skillRepository.findAllById(distinctSkillIds).stream()
				.collect(Collectors.toMap(Skill::getId, Function.identity()));

		if (skillsById.size() != distinctSkillIds.size()) {
			Long missingSkillId = distinctSkillIds.stream()
					.filter(skillId -> !skillsById.containsKey(skillId))
					.findFirst()
					.orElseThrow();
			throw new ResourceNotFoundException("Skill with id %d was not found".formatted(missingSkillId));
		}

		employeeSkillRepository.deleteByEmployee_Id(employee.getId());
		entityManager.flush();

		// Limpa a coleção em memória para evitar cache desatualizado
		employee.getEmployeeSkills().clear();

		List<EmployeeSkill> employeeSkills = request.skills().stream()
				.map(skill -> EmployeeSkill.builder()
						.id(new EmployeeSkillId(employee.getId(), skill.skillId()))
						.employee(employee)
						.skill(skillsById.get(skill.skillId()))
						.proficiencyLevel(skill.proficiencyLevel())
						.yearsExperience(skill.yearsExperience())
						.validatedBy(isSelfProfile ? null : context.userName())
						.build())
				.toList();

		employeeSkillRepository.saveAll(employeeSkills);
		entityManager.flush();

		Employee reloadedEmployee = employeeRepository.findDetailedById(id, context.companyId())
				.orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE_NOT_FOUND_MESSAGE.formatted(id)));
		return employeeMapper.toProfile(reloadedEmployee);
	}
}
