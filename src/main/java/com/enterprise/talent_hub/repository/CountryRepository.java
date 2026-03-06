package com.enterprise.talent_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enterprise.talent_hub.domain.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {

	List<Country> findAllByOrderByNameAsc();
}
