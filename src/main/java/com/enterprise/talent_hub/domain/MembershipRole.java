package com.enterprise.talent_hub.domain;

import java.util.EnumSet;
import java.util.Set;

public enum MembershipRole {
	COMPANY_OWNER(EnumSet.allOf(Permission.class)),
	COMPANY_ADMIN(EnumSet.of(
		Permission.COMPANY_MANAGE,
		Permission.USERS_READ,
		Permission.USERS_CREATE,
		Permission.USERS_UPDATE,
		Permission.USERS_DISABLE,
		Permission.USERS_DELETE,
		Permission.EMPLOYEES_READ,
		Permission.EMPLOYEES_CREATE,
		Permission.EMPLOYEES_UPDATE,
		Permission.EMPLOYEES_DELETE,
		Permission.DASHBOARD_READ
	)),
	HR_MANAGER(EnumSet.of(
		Permission.USERS_READ,
		Permission.USERS_CREATE,
		Permission.USERS_UPDATE,
		Permission.USERS_DISABLE,
		Permission.EMPLOYEES_READ,
		Permission.EMPLOYEES_CREATE,
		Permission.EMPLOYEES_UPDATE,
		Permission.EMPLOYEES_DELETE,
		Permission.DASHBOARD_READ
	)),
	MANAGER(EnumSet.of(
		Permission.EMPLOYEES_READ,
		Permission.DASHBOARD_READ
	)),
	EMPLOYEE_VIEWER(EnumSet.of(
		Permission.EMPLOYEES_READ,
		Permission.DASHBOARD_READ
	)),
	EMPLOYEE_SELF_SERVICE(EnumSet.of(
		Permission.EMPLOYEES_READ,
		Permission.DASHBOARD_READ
	));

	private final Set<Permission> permissions;

	MembershipRole(Set<Permission> permissions) {
		this.permissions = Set.copyOf(permissions);
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}
}
