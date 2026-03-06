package com.enterprise.talent_hub.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee_skills")
public class EmployeeSkill {

	@EmbeddedId
	private EmployeeSkillId id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId("employeeId")
	@JoinColumn(name = "employee_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Employee employee;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId("skillId")
	@JoinColumn(name = "skill_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Skill skill;

	@Enumerated(EnumType.STRING)
	@Column(name = "proficiency_level", nullable = false, length = 20)
	private ProficiencyLevel proficiencyLevel;

	@Column(name = "years_experience", nullable = false)
	private Integer yearsExperience;

	@Column(name = "validated_by", length = 120)
	private String validatedBy;
}
