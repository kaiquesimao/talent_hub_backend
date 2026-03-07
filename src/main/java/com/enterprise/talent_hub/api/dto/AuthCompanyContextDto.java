package com.enterprise.talent_hub.api.dto;

public record AuthCompanyContextDto(
	Long companyId,
	String companyName,
	String companySlug,
	String membershipRole,
	boolean isDefault
) {
}
