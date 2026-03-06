package com.enterprise.talent_hub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.enterprise.talent_hub.domain.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

	@Override
	@EntityGraph(attributePaths = { "country" })
	Page<Employee> findAll(Specification<Employee> spec, Pageable pageable);

	@EntityGraph(attributePaths = { "country", "employeeSkills", "employeeSkills.skill" })
	@Query("select e from Employee e where e.id = :id")
	Optional<Employee> findDetailedById(@Param("id") Long id);

	Optional<Employee> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);
}
