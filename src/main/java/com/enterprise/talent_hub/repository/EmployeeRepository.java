package com.enterprise.talent_hub.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.enterprise.talent_hub.domain.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

	@Override
	@EntityGraph(attributePaths = { "country" })
	Page<Employee> findAll(Specification<Employee> spec, Pageable pageable);

	@EntityGraph(attributePaths = { "country", "employeeSkills", "employeeSkills.skill" })
	@Query("select e from Employee e where e.id = :id and e.company.id = :companyId")
	Optional<Employee> findDetailedById(@Param("id") Long id, @Param("companyId") Long companyId);

	@EntityGraph(attributePaths = { "country", "employeeSkills", "employeeSkills.skill" })
	@Query("select e from Employee e where e.company.id = :companyId and e.user.id = :userId")
	Optional<Employee> findDetailedByCompanyIdAndUserId(@Param("companyId") Long companyId,
			@Param("userId") Long userId);

	List<Employee> findByCompanyIdAndUserIdIn(Long companyId, Collection<Long> userIds);

	Optional<Employee> findByCompanyIdAndUserId(Long companyId, Long userId);

	long countByCompanyId(Long companyId);

	long countDistinctByCompanyIdAndCountryIdIsNotNull(Long companyId);

	@Query("select c.name as label, count(e.id) as value from Employee e join e.country c where e.company.id = :companyId group by c.name order by count(e.id) desc, c.name asc")
	List<DashboardMetricView> countEmployeesByCountry(@Param("companyId") Long companyId);

	@Query("select s.name as label, count(es.id.employeeId) as value from EmployeeSkill es join es.skill s join es.employee e where e.company.id = :companyId group by s.name order by count(es.id.employeeId) desc, s.name asc")
	List<DashboardMetricView> countTopSkillsByCompany(@Param("companyId") Long companyId, Pageable pageable);
}
