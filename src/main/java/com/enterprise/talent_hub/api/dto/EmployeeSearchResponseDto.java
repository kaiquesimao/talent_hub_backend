package com.enterprise.talent_hub.api.dto;

import java.util.List;

public record EmployeeSearchResponseDto(
	List<EmployeeSummaryDto> content,
	int page,
	int size,
	long totalElements,
	int totalPages,
	boolean hasNext,
	boolean hasPrevious
) {
}
