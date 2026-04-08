package com.example.deal.service;

import com.example.api.common.dto.LoanOfferDto;
import com.example.deal.entity.StatementEntity;

import java.util.UUID;

/**
 * Сервис, обслуживающий взаимодействия с заявками
 */
public interface StatementService {
    /**
     * Создание новой заявки
     * @param clientId Идентификатор клиента, на которого создаётся заявка
     * @return Идентификатор заявки для дальнейшего взаимодействия
     */
    UUID createStatement(UUID clientId);

    /**
     * Обновление состояния заявки (или сохранение новой)
     * @param statementEntity Сущность заявки
     */
    void updateStatement(StatementEntity statementEntity);

    /**
     * Получение сущности заявки через её идентификатор
     *
     * @param statementId Идентификатор заявки
     * @return Искомая заявка
     */
    StatementEntity getStatement(UUID statementId);

    /**
     * Обновление статуса заявки после того, как клиент выбрал предложение по кредиту
     * @param loanOfferDto Выбранное предложение
     */
    void updateStatementWithChoosedOffer(LoanOfferDto loanOfferDto);
}
