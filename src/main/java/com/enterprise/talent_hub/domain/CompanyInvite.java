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
@Table(name = "company_invites")
public class CompanyInvite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "company_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Company company;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "invited_by_user_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private AppUser invitedBy;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "country_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Country country;

	@Column(nullable = false, length = 180)
	private String email;

	@Column(name = "full_name", nullable = false, length = 180)
	private String fullName;

	@Column(name = "employee_role", nullable = false, length = 120)
	private String employeeRole;

	@Enumerated(EnumType.STRING)
	@Column(name = "membership_role", nullable = false, length = 40)
	private MembershipRole membershipRole;

	@Column(nullable = false, length = 120, unique = true)
	private String token;

	@Column(name = "expires_at", nullable = false)
	private OffsetDateTime expiresAt;

	@Column(name = "accepted_at")
	private OffsetDateTime acceptedAt;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@PrePersist
	void onCreate() {
		if (membershipRole == null) {
			membershipRole = MembershipRole.EMPLOYEE_SELF_SERVICE;
		}
		if (createdAt == null) {
			createdAt = OffsetDateTime.now();
		}
	}
}
