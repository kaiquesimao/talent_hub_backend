package com.enterprise.talent_hub.api.dto;

public record EmployeeSummaryDto(
		Long id,
		String name,
		String email,
		String role,
		String countryName,
		String countryIsoCode) {
}
