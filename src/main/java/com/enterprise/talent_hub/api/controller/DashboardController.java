package com.enterprise.talent_hub.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enterprise.talent_hub.api.dto.DashboardSummaryDto;
import com.enterprise.talent_hub.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

	private final DashboardService dashboardService;

	@GetMapping
	public DashboardSummaryDto summary() {
		return dashboardService.getSummary();
	}
}
