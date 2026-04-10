package com.example.deal.service;

import com.example.api.common.dto.CreditDto;

import java.util.UUID;

/**
 * Сервис, обслуживающий взаимодействие с кредитами
 */
public interface CreditService {
    UUID createCredit(CreditDto creditDto);
}
