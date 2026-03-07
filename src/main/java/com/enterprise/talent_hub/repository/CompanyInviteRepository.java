package com.enterprise.talent_hub.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.enterprise.talent_hub.domain.CompanyInvite;

public interface CompanyInviteRepository extends JpaRepository<CompanyInvite, Long> {

	@EntityGraph(attributePaths = { "company", "country", "invitedBy" })
	List<CompanyInvite> findByCompanyIdAndAcceptedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(Long companyId,
			OffsetDateTime now);

	@EntityGraph(attributePaths = { "company", "country", "invitedBy" })
	Optional<CompanyInvite> findByToken(String token);

	@EntityGraph(attributePaths = { "company", "country", "invitedBy" })
	Optional<CompanyInvite> findByTokenAndAcceptedAtIsNullAndExpiresAtAfter(String token, OffsetDateTime now);

	@EntityGraph(attributePaths = { "company", "country", "invitedBy" })
	Optional<CompanyInvite> findByCompanyIdAndIdAndAcceptedAtIsNull(Long companyId, Long id);

	boolean existsByCompanyIdAndEmailIgnoreCaseAndAcceptedAtIsNullAndExpiresAtAfter(Long companyId, String email,
			OffsetDateTime now);
}
