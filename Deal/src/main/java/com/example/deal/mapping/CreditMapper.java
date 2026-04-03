package com.example.deal.mapping;

import com.example.deal.dto.CreditDto;
import com.example.deal.entity.CreditEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    @Mapping(target = "creditId", ignore = true)
    CreditEntity toEntity(CreditDto dto);

    CreditDto toDto(CreditEntity entity);
}
