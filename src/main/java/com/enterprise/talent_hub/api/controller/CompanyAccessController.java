package com.enterprise.talent_hub.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enterprise.talent_hub.api.dto.CompanyInviteRequestDto;
import com.enterprise.talent_hub.api.dto.CompanyInviteResponseDto;
import com.enterprise.talent_hub.api.dto.CompanyMembershipSummaryDto;
import com.enterprise.talent_hub.api.dto.CompanyMembershipUpdateRequestDto;
import com.enterprise.talent_hub.service.CompanyAccessService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/company-access")
public class CompanyAccessController {

	private final CompanyAccessService companyAccessService;

	@GetMapping("/memberships")
	public List<CompanyMembershipSummaryDto> memberships() {
		return companyAccessService.listMemberships();
	}

	@GetMapping("/invites")
	public List<CompanyInviteResponseDto> invites() {
		return companyAccessService.listInvites();
	}

	@PostMapping("/invites")
	public CompanyInviteResponseDto createInvite(@Valid @RequestBody CompanyInviteRequestDto request) {
		return companyAccessService.createInvite(request);
	}

	@PatchMapping("/memberships/{membershipId}")
	public CompanyMembershipSummaryDto updateMembership(
		@PathVariable Long membershipId,
		@Valid @RequestBody CompanyMembershipUpdateRequestDto request
	) {
		return companyAccessService.updateMembership(membershipId, request);
	}
}
