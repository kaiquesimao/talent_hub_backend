package com.enterprise.talent_hub.service.auth;

import java.util.Set;

import com.enterprise.talent_hub.domain.MembershipRole;
import com.enterprise.talent_hub.domain.Permission;

public record AuthenticatedCompanyContext(
	Long userId,
	Long companyId,
	Long membershipId,
	String userName,
	String userEmail,
	MembershipRole role,
	Set<Permission> permissions
) {

	public boolean hasPermission(Permission permission) {
		return permissions.contains(permission);
	}
}
