package com.enterprise.talent_hub.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.enterprise.talent_hub.api.dto.SkillDto;
import com.enterprise.talent_hub.domain.Skill;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SkillMapper {

	SkillDto toDto(Skill skill);

	List<SkillDto> toDto(List<Skill> skills);
}
