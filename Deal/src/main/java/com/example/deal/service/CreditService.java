package com.example.deal.service;

import com.example.deal.dto.CreditDto;
import com.example.deal.dto.CreditStatus;
import com.example.deal.entity.CreditEntity;
import com.example.deal.mapping.CreditMapper;
import com.example.deal.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Сервис, обслуживающий взаимодействие с кредитами
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreditService {
    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;

    /**
     * Создаёт запись в кредитах на основе объекта кредита
     * @param creditDto Объект с информацией по кредиту
     * @return Идентификатор кредита для дальнейшего взаимодействия
     */
    public UUID createCredit(CreditDto creditDto) {
        CreditEntity creditEntity = creditMapper.toEntity(creditDto);
        creditEntity.setCreditStatus(CreditStatus.CALCULATED);
        creditRepository.save(creditEntity);
        log.info("Created credit added in database: {}", creditEntity);
        return creditEntity.getCreditId();
    }
}
