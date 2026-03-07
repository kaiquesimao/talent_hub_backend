package com.enterprise.talent_hub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enterprise.talent_hub.domain.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

	Optional<Company> findBySlugIgnoreCase(String slug);

	boolean existsBySlugIgnoreCase(String slug);
}
