package com.enterprise.talent_hub.service.auth;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import com.enterprise.talent_hub.domain.MembershipRole;
import com.enterprise.talent_hub.domain.Permission;

@Service
public class CurrentTenantService {

	public AuthenticatedCompanyContext requireContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
			throw new AccessDeniedException("Authenticated tenant context is not available");
		}

		Jwt jwt = jwtAuthenticationToken.getToken();
		Long userId = getLongClaim(jwt, "userId");
		Long companyId = getLongClaim(jwt, "activeCompanyId");
		Long membershipId = getLongClaim(jwt, "membershipId");
		String userName = jwt.getClaimAsString("name");
		String userEmail = jwt.getClaimAsString("email");
		String roleValue = jwt.getClaimAsString("role");
		MembershipRole role = roleValue == null ? MembershipRole.EMPLOYEE_SELF_SERVICE : MembershipRole.valueOf(roleValue);
		Set<Permission> permissions = new HashSet<>();
		for (String permission : jwt.getClaimAsStringList("permissions")) {
			for (Permission candidate : Permission.values()) {
				if (candidate.getCode().equals(permission)) {
					permissions.add(candidate);
					break;
				}
			}
		}
		if (permissions.isEmpty()) {
			permissions.addAll(role.getPermissions());
		}
		return new AuthenticatedCompanyContext(userId, companyId, membershipId, userName, userEmail, role, Set.copyOf(permissions));
	}

	public void requirePermission(Permission permission) {
		AuthenticatedCompanyContext context = requireContext();
		if (!context.hasPermission(permission)) {
			throw new AccessDeniedException("You do not have permission to perform this action");
		}
	}

	private Long getLongClaim(Jwt jwt, String claimName) {
		Object claim = jwt.getClaims().get(claimName);
		if (claim instanceof Number number) {
			return number.longValue();
		}
		if (claim instanceof String stringValue && !stringValue.isBlank()) {
			return Long.valueOf(stringValue);
		}
		throw new AccessDeniedException("Missing JWT claim: " + claimName);
	}
}
