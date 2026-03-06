package com.enterprise.talent_hub.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.enterprise.talent_hub.api.dto.CountryDto;
import com.enterprise.talent_hub.domain.Country;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CountryMapper {

	CountryDto toDto(Country country);

	List<CountryDto> toDto(List<Country> countries);
}
