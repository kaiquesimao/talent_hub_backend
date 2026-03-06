package com.enterprise.talent_hub.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class EmployeeSkillId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "employee_id")
	private Long employeeId;

	@Column(name = "skill_id")
	private Long skillId;
}
