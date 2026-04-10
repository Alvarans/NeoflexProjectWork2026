package com.example.deal.service.impl;

import com.example.api.common.dto.*;
import com.example.deal.dto.CreditStatus;
import com.example.deal.entity.CreditEntity;
import com.example.deal.mapping.CreditMapper;
import com.example.deal.repository.CreditRepository;
import com.example.deal.service.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Сервис, реализующий взаимодействие с кредитами
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreditServiceImpl implements CreditService {
    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;

    /**
     * Создаёт запись в кредитах на основе объекта кредита
     * @param creditDto Объект с информацией по кредиту
     * @return Идентификатор кредита для дальнейшего взаимодействия
     */
    public UUID createCredit(CreditDto creditDto) {
        if (creditDto == null) {
            log.error("credit dto is null. Cannot create credit object.");
            throw new IllegalArgumentException("credit dto cannot be null");
        }
        CreditEntity creditEntity = creditMapper.toCreditEntity(creditDto);
        creditEntity.setCreditStatus(CreditStatus.CALCULATED);
        creditRepository.save(creditEntity);
        log.info("Created credit added in database: {}", creditEntity);
        return creditEntity.getCreditId();
    }
}
