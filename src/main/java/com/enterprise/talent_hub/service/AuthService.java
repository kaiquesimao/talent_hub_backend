package com.enterprise.talent_hub.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enterprise.talent_hub.api.dto.AuthAcceptInviteRequestDto;
import com.enterprise.talent_hub.api.dto.AuthCompanyContextDto;
import com.enterprise.talent_hub.api.dto.AuthInviteDetailsDto;
import com.enterprise.talent_hub.api.dto.AuthLoginRequestDto;
import com.enterprise.talent_hub.api.dto.AuthLoginResponseDto;
import com.enterprise.talent_hub.api.dto.AuthRegisterCompanyRequestDto;
import com.enterprise.talent_hub.api.dto.AuthSwitchCompanyRequestDto;
import com.enterprise.talent_hub.config.AppSecurityProperties;
import com.enterprise.talent_hub.domain.AppUser;
import com.enterprise.talent_hub.domain.Company;
import com.enterprise.talent_hub.domain.CompanyInvite;
import com.enterprise.talent_hub.domain.CompanyMembership;
import com.enterprise.talent_hub.domain.CompanyStatus;
import com.enterprise.talent_hub.domain.Employee;
import com.enterprise.talent_hub.domain.MembershipRole;
import com.enterprise.talent_hub.domain.MembershipStatus;
import com.enterprise.talent_hub.domain.Permission;
import com.enterprise.talent_hub.domain.UserStatus;
import com.enterprise.talent_hub.repository.AppUserRepository;
import com.enterprise.talent_hub.repository.CompanyInviteRepository;
import com.enterprise.talent_hub.repository.CompanyMembershipRepository;
import com.enterprise.talent_hub.repository.CompanyRepository;
import com.enterprise.talent_hub.repository.CountryRepository;
import com.enterprise.talent_hub.repository.EmployeeRepository;
import com.enterprise.talent_hub.service.auth.AuthenticatedCompanyContext;
import com.enterprise.talent_hub.service.auth.CurrentTenantService;
import com.enterprise.talent_hub.service.exception.ConflictException;
import com.enterprise.talent_hub.service.exception.InvalidCredentialsException;
import com.enterprise.talent_hub.service.exception.InvalidInviteException;
import com.enterprise.talent_hub.service.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AppUserRepository appUserRepository;
	private final CompanyRepository companyRepository;
	private final CompanyInviteRepository companyInviteRepository;
	private final CompanyMembershipRepository companyMembershipRepository;
	private final CountryRepository countryRepository;
	private final EmployeeRepository employeeRepository;
	private final AppSecurityProperties securityProperties;
	private final JwtEncoder jwtEncoder;
	private final PasswordEncoder passwordEncoder;
	private final CurrentTenantService currentTenantService;

	@Transactional(readOnly = true)
	public AuthLoginResponseDto login(AuthLoginRequestDto request) {
		String normalizedEmail = request.email().trim().toLowerCase();
		AppUser user = appUserRepository.findByEmailIgnoreCase(normalizedEmail)
			.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

		if (user.getStatus() != UserStatus.ACTIVE
				|| !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new InvalidCredentialsException("Invalid email or password");
		}

		List<CompanyMembership> memberships = activeMembershipsForUser(user.getId());
		if (memberships.isEmpty()) {
			throw new InvalidCredentialsException("You do not have an active company membership");
		}

		user.setLastLoginAt(OffsetDateTime.now());
		appUserRepository.save(user);

		return buildLoginResponse(user, memberships.getFirst(), memberships);
	}

	@Transactional
	public AuthLoginResponseDto registerCompany(AuthRegisterCompanyRequestDto request) {
		String normalizedSlug = normalizeSlug(request.companySlug());
		String normalizedEmail = request.adminEmail().trim().toLowerCase(Locale.ROOT);
		if (companyRepository.existsBySlugIgnoreCase(normalizedSlug)) {
			throw new ConflictException("A company with this slug already exists");
		}
		if (appUserRepository.existsByEmailIgnoreCase(normalizedEmail)) {
			throw new ConflictException("A user with this email already exists");
		}

		var country = countryRepository.findById(request.adminCountryId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Country with id %d was not found".formatted(request.adminCountryId())));

		Company company = companyRepository.save(Company.builder()
				.name(request.companyName().trim())
				.slug(normalizedSlug)
				.status(CompanyStatus.ACTIVE)
				.build());

		AppUser user = appUserRepository.save(AppUser.builder()
				.name(request.adminName().trim())
				.email(normalizedEmail)
				.passwordHash(passwordEncoder.encode(request.password()))
				.status(UserStatus.ACTIVE)
				.build());

		CompanyMembership membership = companyMembershipRepository.save(CompanyMembership.builder()
				.company(company)
				.user(user)
				.role(MembershipRole.COMPANY_OWNER)
				.status(MembershipStatus.ACTIVE)
				.isDefault(true)
				.build());

		employeeRepository.save(Employee.builder()
				.name(request.adminName().trim())
				.email(normalizedEmail)
				.role(request.adminRole().trim())
				.company(company)
				.user(user)
				.country(country)
				.build());

		return buildLoginResponse(user, membership, List.of(membership));
	}

	@Transactional(readOnly = true)
	public List<AuthCompanyContextDto> listCompanies() {
		AuthenticatedCompanyContext context = currentTenantService.requireContext();
		return activeMembershipsForUser(context.userId()).stream()
				.map(this::toCompanyContext)
				.toList();
	}

	@Transactional(readOnly = true)
	public AuthLoginResponseDto switchCompany(AuthSwitchCompanyRequestDto request) {
		AuthenticatedCompanyContext context = currentTenantService.requireContext();
		CompanyMembership selectedMembership = companyMembershipRepository
				.findByUserIdAndCompanyIdAndStatus(context.userId(), request.companyId(), MembershipStatus.ACTIVE)
				.orElseThrow(() -> new AccessDeniedException("You do not belong to this company"));

		List<CompanyMembership> memberships = activeMembershipsForUser(context.userId());
		return buildLoginResponse(selectedMembership.getUser(), selectedMembership, memberships);
	}

	@Transactional(readOnly = true)
	public AuthInviteDetailsDto getInviteDetails(String token) {
		CompanyInvite invite = companyInviteRepository
				.findByTokenAndAcceptedAtIsNullAndExpiresAtAfter(token, OffsetDateTime.now())
				.orElseThrow(() -> new InvalidInviteException("Invite is invalid or has expired"));
		boolean existingUser = appUserRepository.existsByEmailIgnoreCase(invite.getEmail());
		return new AuthInviteDetailsDto(
				invite.getCompany().getName(),
				invite.getCompany().getSlug(),
				invite.getEmail(),
				invite.getFullName(),
				invite.getEmployeeRole(),
				invite.getMembershipRole().name(),
				invite.getExpiresAt(),
				existingUser);
	}

	@Transactional
	public AuthLoginResponseDto acceptInvite(AuthAcceptInviteRequestDto request) {
		CompanyInvite invite = companyInviteRepository
				.findByTokenAndAcceptedAtIsNullAndExpiresAtAfter(request.token(), OffsetDateTime.now())
				.orElseThrow(() -> new InvalidInviteException("Invite is invalid or has expired"));

		String normalizedEmail = invite.getEmail().trim().toLowerCase(Locale.ROOT);
		AppUser user = appUserRepository.findByEmailIgnoreCase(normalizedEmail)
				.map(existingUser -> validateExistingInvitedUser(existingUser, request.password(), invite))
				.orElseGet(() -> appUserRepository.save(AppUser.builder()
						.name(invite.getFullName())
						.email(normalizedEmail)
						.passwordHash(passwordEncoder.encode(request.password()))
						.status(UserStatus.ACTIVE)
						.build()));

		if (companyMembershipRepository.existsByCompanyIdAndUserId(invite.getCompany().getId(), user.getId())) {
			throw new ConflictException("This user already belongs to the invited company");
		}

		CompanyMembership membership = companyMembershipRepository.save(CompanyMembership.builder()
				.company(invite.getCompany())
				.user(user)
				.role(invite.getMembershipRole())
				.status(MembershipStatus.ACTIVE)
				.isDefault(companyMembershipRepository.countByUserId(user.getId()) == 0)
				.build());

		employeeRepository.save(Employee.builder()
				.name(invite.getFullName())
				.email(normalizedEmail)
				.role(invite.getEmployeeRole())
				.company(invite.getCompany())
				.user(user)
				.country(invite.getCountry())
				.build());

		invite.setAcceptedAt(OffsetDateTime.now());
		companyInviteRepository.save(invite);

		List<CompanyMembership> memberships = activeMembershipsForUser(user.getId());
		return buildLoginResponse(user, membership, memberships);
	}

	private List<CompanyMembership> activeMembershipsForUser(Long userId) {
		return companyMembershipRepository
				.findByUserIdAndStatusOrderByIsDefaultDescCreatedAtAsc(userId, MembershipStatus.ACTIVE).stream()
				.filter(membership -> membership.getCompany().getStatus() == CompanyStatus.ACTIVE)
				.sorted(Comparator.comparing(CompanyMembership::isDefault).reversed()
						.thenComparing(CompanyMembership::getCreatedAt))
				.toList();
	}

	private AuthLoginResponseDto buildLoginResponse(
			AppUser user,
			CompanyMembership activeMembership,
			List<CompanyMembership> memberships) {
		Instant issuedAt = Instant.now();
		Instant expiresAt = issuedAt.plus(securityProperties.getJwt().getTtl());
		List<String> permissions = activeMembership.getRole().getPermissions().stream()
				.map(Permission::getCode)
				.sorted()
				.toList();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.subject(user.getEmail())
			.issuedAt(issuedAt)
			.expiresAt(expiresAt)
				.claim("scope", "talent.read tenant.read")
				.claim("userId", user.getId())
				.claim("activeCompanyId", activeMembership.getCompany().getId())
				.claim("membershipId", activeMembership.getId())
				.claim("companySlug", activeMembership.getCompany().getSlug())
				.claim("role", activeMembership.getRole().name())
				.claim("permissions", permissions)
				.claim("email", user.getEmail())
				.claim("name", user.getName())
			.build();
		JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
		String token = jwtEncoder.encode(JwtEncoderParameters.from(
			jwsHeader,
			claims
		)).getTokenValue();

		return new AuthLoginResponseDto(
			token,
			"Bearer",
			securityProperties.getJwt().getTtl().toSeconds(),
				activeMembership.getCompany().getId(),
				activeMembership.getCompany().getName(),
				activeMembership.getCompany().getSlug(),
				activeMembership.getRole().name(),
				user.getName(),
				user.getEmail(),
				permissions,
				memberships.stream().map(this::toCompanyContext).toList());
	}

	private AuthCompanyContextDto toCompanyContext(CompanyMembership membership) {
		return new AuthCompanyContextDto(
				membership.getCompany().getId(),
				membership.getCompany().getName(),
				membership.getCompany().getSlug(),
				membership.getRole().name(),
				membership.isDefault()
		);
	}

	private String normalizeSlug(String value) {
		String normalized = value.trim().toLowerCase(Locale.ROOT)
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("(^-+|-+$)", "");
		if (normalized.length() < 2) {
			throw new IllegalArgumentException("Company slug must contain at least 2 valid characters");
		}
		return normalized;
	}

	private AppUser validateExistingInvitedUser(AppUser existingUser, String rawPassword, CompanyInvite invite) {
		if (existingUser.getStatus() != UserStatus.ACTIVE) {
			throw new ConflictException("The invited user is disabled and cannot accept invites");
		}
		if (!passwordEncoder.matches(rawPassword, existingUser.getPasswordHash())) {
			throw new InvalidInviteException("Existing users must provide their current password to accept the invite");
		}
		if (companyMembershipRepository.existsByCompanyIdAndUserId(invite.getCompany().getId(), existingUser.getId())) {
			throw new ConflictException("This user already belongs to the invited company");
		}
		return existingUser;
	}
}
