package com.enterprise.talent_hub.domain;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
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
@Table(name = "employees")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 180)
	private String name;

	@Column(nullable = false, length = 180, unique = true)
	private String email;

	@Column(nullable = false, length = 120)
	private String role;

	@ManyToOne(optional = false)
	@JoinColumn(name = "country_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Country country;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@Builder.Default
	@OneToMany(mappedBy = "employee")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Set<EmployeeSkill> employeeSkills = new LinkedHashSet<>();

	@PrePersist
	void onCreate() {
		if (createdAt == null) {
			createdAt = OffsetDateTime.now();
		}
	}
}
