package com.enterprise.talent_hub.api.dto;

import java.util.List;

public record DashboardSummaryDto(
	long activeUsers,
	long totalEmployees,
	long countriesRepresented,
	long trackedSkills,
	List<DashboardMetricItemDto> employeesByCountry,
	List<DashboardMetricItemDto> topSkills
) {
}
