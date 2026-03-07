package com.enterprise.talent_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.enterprise.talent_hub.domain.CompanyMembership;
import com.enterprise.talent_hub.domain.MembershipRole;
import com.enterprise.talent_hub.domain.MembershipStatus;

public interface CompanyMembershipRepository extends JpaRepository<CompanyMembership, Long> {

	@EntityGraph(attributePaths = { "company", "user" })
	List<CompanyMembership> findByUserIdAndStatusOrderByIsDefaultDescCreatedAtAsc(Long userId, MembershipStatus status);

	@EntityGraph(attributePaths = { "company", "user" })
	Optional<CompanyMembership> findByUserIdAndCompanyIdAndStatus(Long userId, Long companyId, MembershipStatus status);

	@EntityGraph(attributePaths = { "company", "user" })
	List<CompanyMembership> findByCompanyIdOrderByCreatedAtAsc(Long companyId);

	@EntityGraph(attributePaths = { "company", "user" })
	Optional<CompanyMembership> findByCompanyIdAndId(Long companyId, Long id);

	boolean existsByCompanyIdAndUserId(Long companyId, Long userId);

	long countByCompanyIdAndStatus(Long companyId, MembershipStatus status);

	long countByCompanyIdAndRoleAndStatus(Long companyId, MembershipRole role, MembershipStatus status);

	long countByUserId(Long userId);
}
