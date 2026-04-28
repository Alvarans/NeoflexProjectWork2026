package com.example.deal.service;

import com.example.api.common.dto.LoanOfferDto;
import com.example.deal.dto.ApplicationStatus;
import com.example.deal.entity.StatementEntity;

import java.util.UUID;

/**
 * Сервис, обслуживающий взаимодействия с заявками
 */
public interface StatementService {
    /**
     * Создание новой заявки
     *
     * @param clientId Идентификатор клиента, на которого создаётся заявка
     * @return Идентификатор заявки для дальнейшего взаимодействия
     */
    UUID createStatement(UUID clientId);

    /**
     * Обновление состояния заявки (или сохранение новой)
     *
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
     * Получение сущности записи для обновления данных в ней с функцией блокировки
     *
     * @param statementId Идентификационный номер сделки
     * @return Сущность сделки
     */
    StatementEntity getStatementForUpdate(UUID statementId);

    /**
     * Получение почты клиента для отправки ему уведомлений
     *
     * @param statementId Идентификационный номер сделки
     * @return Электронная почта клиента
     */
    String getClientEmailFromStatementByStatementId(UUID statementId);

    /**
     * Обновление статуса заявки после того, как клиент выбрал предложение по кредиту
     *
     * @param loanOfferDto Выбранное предложение
     */
    void updateStatementWithChoosedOffer(LoanOfferDto loanOfferDto);

    /**
     * Подписание сделки
     *
     * @param statementId Идентификатор сделки
     * @param sesCode     Подпись
     */
    void signStatementWithSignCode(UUID statementId, String sesCode);

    /**
     * Обновление статуса хода сделки
     *
     * @param statementId       Идентификатор сделки
     * @param applicationStatus Новый статус сделки
     */
    void updateStatementStatus(UUID statementId, ApplicationStatus applicationStatus);
}
