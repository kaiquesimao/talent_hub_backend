package com.enterprise.talent_hub.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enterprise.talent_hub.api.dto.DashboardMetricItemDto;
import com.enterprise.talent_hub.api.dto.DashboardSummaryDto;
import com.enterprise.talent_hub.domain.MembershipStatus;
import com.enterprise.talent_hub.domain.Permission;
import com.enterprise.talent_hub.repository.CompanyMembershipRepository;
import com.enterprise.talent_hub.repository.DashboardMetricView;
import com.enterprise.talent_hub.repository.EmployeeRepository;
import com.enterprise.talent_hub.repository.SkillRepository;
import com.enterprise.talent_hub.service.auth.AuthenticatedCompanyContext;
import com.enterprise.talent_hub.service.auth.CurrentTenantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

	private final EmployeeRepository employeeRepository;
	private final CompanyMembershipRepository companyMembershipRepository;
	private final SkillRepository skillRepository;
	private final CurrentTenantService currentTenantService;

	public DashboardSummaryDto getSummary() {
		AuthenticatedCompanyContext context = currentTenantService.requireContext();
		currentTenantService.requirePermission(Permission.DASHBOARD_READ);
		Long companyId = context.companyId();
		return new DashboardSummaryDto(
			companyMembershipRepository.countByCompanyIdAndStatus(companyId, MembershipStatus.ACTIVE),
			employeeRepository.countByCompanyId(companyId),
			employeeRepository.countDistinctByCompanyIdAndCountryIdIsNotNull(companyId),
			skillRepository.count(),
			toMetricItems(employeeRepository.countEmployeesByCountry(companyId)),
			toMetricItems(employeeRepository.countTopSkillsByCompany(companyId, PageRequest.of(0, 5)))
		);
	}

	private List<DashboardMetricItemDto> toMetricItems(List<DashboardMetricView> views) {
		return views.stream()
			.map(view -> new DashboardMetricItemDto(view.getLabel(), view.getValue()))
			.toList();
	}
}
