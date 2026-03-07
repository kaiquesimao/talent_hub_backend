package com.enterprise.talent_hub.domain;

public enum Permission {
	COMPANY_MANAGE("company.manage"),
	USERS_READ("users.read"),
	USERS_CREATE("users.create"),
	USERS_UPDATE("users.update"),
	USERS_DISABLE("users.disable"),
	USERS_DELETE("users.delete"),
	EMPLOYEES_READ("employees.read"),
	EMPLOYEES_CREATE("employees.create"),
	EMPLOYEES_UPDATE("employees.update"),
	EMPLOYEES_DELETE("employees.delete"),
	DASHBOARD_READ("dashboard.read");

	private final String code;

	Permission(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
