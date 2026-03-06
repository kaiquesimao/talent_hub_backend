package com.enterprise.talent_hub.service.specification;

import static org.springframework.util.StringUtils.hasText;

import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.enterprise.talent_hub.domain.Employee;
import com.enterprise.talent_hub.domain.ProficiencyLevel;

import jakarta.persistence.criteria.JoinType;

public final class EmployeeSpecifications {

	private EmployeeSpecifications() {
	}

	public static Specification<Employee> withFilters(
		String query,
		Long countryId,
		Long skillId,
		ProficiencyLevel proficiencyLevel
	) {
		Specification<Employee> specification = Specification.where(distinct());

		if (hasText(query)) {
			specification = specification.and(textSearch(query));
		}
		if (countryId != null) {
			specification = specification.and(hasCountryId(countryId));
		}
		if (skillId != null) {
			specification = specification.and(hasSkillId(skillId));
		}
		if (proficiencyLevel != null) {
			specification = specification.and(hasProficiencyLevel(proficiencyLevel));
		}

		return specification;
	}

	private static Specification<Employee> distinct() {
		return (root, query, criteriaBuilder) -> {
			query.distinct(true);
			return criteriaBuilder.conjunction();
		};
	}

	private static Specification<Employee> textSearch(String query) {
		return (root, criteriaQuery, criteriaBuilder) -> {
			String wildcard = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";

			return criteriaBuilder.or(
				criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), wildcard),
				criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), wildcard),
				criteriaBuilder.like(criteriaBuilder.lower(root.get("role")), wildcard)
			);
		};
	}

	private static Specification<Employee> hasCountryId(Long countryId) {
		return (root, criteriaQuery, criteriaBuilder) ->
			criteriaBuilder.equal(root.join("country", JoinType.INNER).get("id"), countryId);
	}

	private static Specification<Employee> hasSkillId(Long skillId) {
		return (root, criteriaQuery, criteriaBuilder) ->
			criteriaBuilder.equal(
				root.join("employeeSkills", JoinType.INNER)
					.join("skill", JoinType.INNER)
					.get("id"),
				skillId
			);
	}

	private static Specification<Employee> hasProficiencyLevel(ProficiencyLevel proficiencyLevel) {
		return (root, criteriaQuery, criteriaBuilder) ->
			criteriaBuilder.equal(
				root.join("employeeSkills", JoinType.INNER).get("proficiencyLevel"),
				proficiencyLevel
			);
	}
}
