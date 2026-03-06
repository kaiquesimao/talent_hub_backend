package com.enterprise.talent_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enterprise.talent_hub.domain.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {

	List<Skill> findAllByOrderByNameAsc();
}
