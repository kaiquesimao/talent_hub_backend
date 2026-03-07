package com.enterprise.talent_hub.domain;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "users")
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 180)
	private String name;

	@Column(nullable = false, length = 180, unique = true)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserStatus status;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@Column(name = "last_login_at")
	private OffsetDateTime lastLoginAt;

	@Builder.Default
	@OneToMany(mappedBy = "user")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Set<CompanyMembership> memberships = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "user")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Set<Employee> employees = new LinkedHashSet<>();

	@PrePersist
	void onCreate() {
		if (status == null) {
			status = UserStatus.ACTIVE;
		}
		if (createdAt == null) {
			createdAt = OffsetDateTime.now();
		}
	}
}
