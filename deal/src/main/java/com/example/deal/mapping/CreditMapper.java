package com.example.deal.mapping;

import com.example.api.common.dto.*;
import com.example.deal.entity.CreditEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер из сущности кредита в объект передачи и наоборот
 */
@Mapper(componentModel = "spring")
public interface CreditMapper {
    @Mapping(target = "creditId", ignore = true)
    @Mapping(target = "creditStatus", ignore = true)
    @Mapping(target = "paymentSchedule", source = "paymentSchedule")
    CreditEntity toCreditEntity(CreditDto dto);

    @Mapping(target = "paymentSchedule", source = "paymentSchedule")
    CreditDto toCreditDto(CreditEntity entity);
}
