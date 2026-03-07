package com.enterprise.talent_hub.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enterprise.talent_hub.api.dto.CompanyInviteRequestDto;
import com.enterprise.talent_hub.api.dto.CompanyInviteResponseDto;
import com.enterprise.talent_hub.api.dto.CompanyMembershipSummaryDto;
import com.enterprise.talent_hub.api.dto.CompanyMembershipUpdateRequestDto;
import com.enterprise.talent_hub.config.AppSecurityProperties;
import com.enterprise.talent_hub.domain.AppUser;
import com.enterprise.talent_hub.domain.Company;
import com.enterprise.talent_hub.domain.CompanyInvite;
import com.enterprise.talent_hub.domain.CompanyMembership;
import com.enterprise.talent_hub.domain.Employee;
import com.enterprise.talent_hub.domain.MembershipRole;
import com.enterprise.talent_hub.domain.MembershipStatus;
import com.enterprise.talent_hub.domain.Permission;
import com.enterprise.talent_hub.repository.AppUserRepository;
import com.enterprise.talent_hub.repository.CompanyInviteRepository;
import com.enterprise.talent_hub.repository.CompanyMembershipRepository;
import com.enterprise.talent_hub.repository.CompanyRepository;
import com.enterprise.talent_hub.repository.CountryRepository;
import com.enterprise.talent_hub.repository.EmployeeRepository;
import com.enterprise.talent_hub.service.auth.AuthenticatedCompanyContext;
import com.enterprise.talent_hub.service.auth.CurrentTenantService;
import com.enterprise.talent_hub.service.exception.ConflictException;
import com.enterprise.talent_hub.service.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyAccessService {

	private final CurrentTenantService currentTenantService;
	private final CompanyMembershipRepository companyMembershipRepository;
	private final CompanyInviteRepository companyInviteRepository;
	private final EmployeeRepository employeeRepository;
	private final CountryRepository countryRepository;
	private final AppUserRepository appUserRepository;
	private final CompanyRepository companyRepository;
	private final AppSecurityProperties securityProperties;

	public List<CompanyMembershipSummaryDto> listMemberships() {
		AuthenticatedCompanyContext context = currentTenantService.requireContext();
		currentTenantService.requirePermission(Permission.USERS_READ);
		List<CompanyMembership> memberships = companyMembershipRepository.findByCompanyIdOrderByCreatedAtAsc(context.companyId());
		Map<Long, Employee> employeesByUserId = employeeRepository
			.findByCompanyIdAndUserIdIn(context.companyId(), memberships.stream().map(membership -> membership.getUser().getId()).toList())
			.stream()
			.collect(Collectors.toMap(employee -> employee.getUser().getId(), Function.identity()));
		return memberships.stream()
			.map(membership -> toMembershipDto(membership, employeesByUserId.get(membership.getUser().getId())))
			.toList();
	}

	public List<CompanyInviteResponseDto> listInvites() {
		AuthenticatedCompanyContext context = currentTenantService.requireContext();
		currentTenantService.requirePermission(Permission.USERS_READ);
		return companyInviteRepository
			.findByCompanyIdAndAcceptedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(context.companyId(), OffsetDateTime.now())
			.stream()
			.map(this::toInviteDto)
			.toList();
	}

	@Transactional
	public CompanyInviteResponseDto createInvite(CompanyInviteRequestDto request) {
		AuthenticatedCompanyContext context = currentTenantService.requireContext();
		currentTenantService.requirePermission(Permission.USERS_CREATE);
		String normalizedEmail = request.email().trim().toLowerCase();
		if (companyInviteRepository.existsByCompanyIdAndEmailIgnoreCaseAndAcceptedAtIsNullAndExpiresAtAfter(
			context.companyId(),
			normalizedEmail,
			OffsetDateTime.now()
		)) {
			throw new ConflictException("There is already an active invite for this email in the current company");
		}

		AppUser existingUser = appUserRepository.findByEmailIgnoreCase(normalizedEmail).orElse(null);
		if (existingUser != null && companyMembershipRepository.existsByCompanyIdAndUserId(context.companyId(), existingUser.getId())) {
			throw new ConflictException("This user already belongs to the current company");
		}

		var country = countryRepository.findById(request.countryId())
			.orElseThrow(() -> new ResourceNotFoundException("Country with id %d was not found".formatted(request.countryId())));
		Company company = companyRepository.findById(context.companyId())
			.orElseThrow(() -> new ResourceNotFoundException("Company with id %d was not found".formatted(context.companyId())));
		AppUser inviter = appUserRepository.findById(context.userId())
			.orElseThrow(() -> new ResourceNotFoundException("User with id %d was not found".formatted(context.userId())));

		CompanyInvite invite = companyInviteRepository.save(CompanyInvite.builder()
			.company(company)
			.invitedBy(inviter)
			.country(country)
			.email(normalizedEmail)
			.fullName(request.fullName().trim())
			.employeeRole(request.employeeRole().trim())
			.membershipRole(request.membershipRole())
			.token(generateInviteToken())
			.expiresAt(OffsetDateTime.now().plus(securityProperties.getInvite().getTtl()))
			.build());

		return toInviteDto(invite);
	}

	@Transactional
	public CompanyMembershipSummaryDto updateMembership(Long membershipId, CompanyMembershipUpdateRequestDto request) {
		AuthenticatedCompanyContext context = currentTenantService.requireContext();
		currentTenantService.requirePermission(Permission.USERS_UPDATE);
		CompanyMembership membership = companyMembershipRepository.findByCompanyIdAndId(context.companyId(), membershipId)
			.orElseThrow(() -> new ResourceNotFoundException("Membership with id %d was not found".formatted(membershipId)));

		if (membership.getId().equals(context.membershipId()) && request.membershipStatus() != MembershipStatus.ACTIVE) {
			throw new ConflictException("You cannot disable your current active membership");
		}

		boolean removingActiveOwner = membership.getRole() == MembershipRole.COMPANY_OWNER
			&& (request.membershipRole() != MembershipRole.COMPANY_OWNER || request.membershipStatus() != MembershipStatus.ACTIVE);
		if (removingActiveOwner
			&& companyMembershipRepository.countByCompanyIdAndRoleAndStatus(context.companyId(), MembershipRole.COMPANY_OWNER, MembershipStatus.ACTIVE) <= 1) {
			throw new ConflictException("The company must keep at least one active owner");
		}

		membership.setRole(request.membershipRole());
		membership.setStatus(request.membershipStatus());
		CompanyMembership savedMembership = companyMembershipRepository.save(membership);
		Employee employee = employeeRepository.findByCompanyIdAndUserId(context.companyId(), savedMembership.getUser().getId()).orElse(null);
		return toMembershipDto(savedMembership, employee);
	}

	private CompanyMembershipSummaryDto toMembershipDto(CompanyMembership membership, Employee employee) {
		return new CompanyMembershipSummaryDto(
			membership.getId(),
			membership.getUser().getId(),
			employee == null ? null : employee.getId(),
			employee == null ? membership.getUser().getName() : employee.getName(),
			membership.getUser().getEmail(),
			employee == null ? null : employee.getRole(),
			membership.getRole().name(),
			membership.getStatus().name(),
			membership.isDefault(),
			membership.getCreatedAt()
		);
	}

	private CompanyInviteResponseDto toInviteDto(CompanyInvite invite) {
		String acceptUrlBase = securityProperties.getInvite().getAcceptUrlBase();
		String acceptUrl = acceptUrlBase.contains("?")
			? acceptUrlBase + "&token=" + invite.getToken()
			: acceptUrlBase + "?token=" + invite.getToken();
		return new CompanyInviteResponseDto(
			invite.getId(),
			invite.getEmail(),
			invite.getFullName(),
			invite.getEmployeeRole(),
			invite.getCountry().getId(),
			invite.getCountry().getName(),
			invite.getMembershipRole().name(),
			invite.getExpiresAt(),
			invite.getCreatedAt(),
			invite.getToken(),
			acceptUrl
		);
	}

	private String generateInviteToken() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
