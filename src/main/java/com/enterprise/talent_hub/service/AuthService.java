package com.enterprise.talent_hub.service;

import java.time.Instant;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enterprise.talent_hub.api.dto.AuthLoginRequestDto;
import com.enterprise.talent_hub.api.dto.AuthLoginResponseDto;
import com.enterprise.talent_hub.config.AppSecurityProperties;
import com.enterprise.talent_hub.domain.Employee;
import com.enterprise.talent_hub.repository.EmployeeRepository;
import com.enterprise.talent_hub.service.exception.InvalidCredentialsException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	private final EmployeeRepository employeeRepository;
	private final AppSecurityProperties securityProperties;
	private final JwtEncoder jwtEncoder;

	public AuthLoginResponseDto login(AuthLoginRequestDto request) {
		String normalizedEmail = request.email().trim().toLowerCase();
		AppSecurityProperties.Demo demo = securityProperties.getDemo();
		Employee employee = employeeRepository.findByEmailIgnoreCase(normalizedEmail)
			.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

		boolean validDemoCredentials = normalizedEmail.equalsIgnoreCase(demo.getEmail())
			&& request.password().equals(demo.getPassword());

		if (!validDemoCredentials) {
			throw new InvalidCredentialsException("Invalid email or password");
		}

		Instant issuedAt = Instant.now();
		Instant expiresAt = issuedAt.plus(securityProperties.getJwt().getTtl());
		JwtClaimsSet claims = JwtClaimsSet.builder()
			.subject(normalizedEmail)
			.issuedAt(issuedAt)
			.expiresAt(expiresAt)
			.claim("scope", "talent.read")
			.claim("email", normalizedEmail)
			.claim("name", employee.getName())
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
			employee.getName(),
			normalizedEmail
		);
	}
}
