package com.enterprise.talent_hub.domain;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "company_memberships")
public class CompanyMembership {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "company_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Company company;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private AppUser user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private MembershipRole role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MembershipStatus status;

	@Column(name = "is_default", nullable = false)
	private boolean isDefault;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@PrePersist
	void onCreate() {
		if (role == null) {
			role = MembershipRole.EMPLOYEE_SELF_SERVICE;
		}
		if (status == null) {
			status = MembershipStatus.ACTIVE;
		}
		if (createdAt == null) {
			createdAt = OffsetDateTime.now();
		}
	}
}
