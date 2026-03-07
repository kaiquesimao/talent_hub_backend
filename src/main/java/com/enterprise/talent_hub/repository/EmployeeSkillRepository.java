package com.enterprise.talent_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enterprise.talent_hub.domain.EmployeeSkill;
import com.enterprise.talent_hub.domain.EmployeeSkillId;

public interface EmployeeSkillRepository extends JpaRepository<EmployeeSkill, EmployeeSkillId> {

	void deleteByEmployee_Id(Long employeeId);
}
