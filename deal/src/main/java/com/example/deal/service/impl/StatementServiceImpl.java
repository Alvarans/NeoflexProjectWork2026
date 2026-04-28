package com.example.deal.service.impl;

import com.example.api.common.dto.LoanOfferDto;
import com.example.deal.dto.ApplicationStatus;
import com.example.deal.entity.StatementEntity;
import com.example.deal.repository.StatementRepository;
import com.example.deal.service.ClientService;
import com.example.deal.service.KafkaProducerService;
import com.example.deal.service.StatementService;
import com.example.dossier.dto.EmailMessage;
import com.example.dossier.dto.EmailTheme;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сервис, реализующий взаимодействия с заявками
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatementServiceImpl implements StatementService {
    private final StatementRepository statementRepository;
    private final ClientService clientService;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Создание новой заявки
     *
     * @param clientId Идентификатор клиента, на которого создаётся заявка
     * @return Идентификатор заявки для дальнейшего взаимодействия
     */
    public UUID createStatement(UUID clientId) {
        StatementEntity statementEntity = new StatementEntity();
        statementEntity.setClient(clientService.getClientEntityByClientId(clientId));
        statementEntity.setApplicationStatus(ApplicationStatus.PREAPPROVAL);
        statementEntity.setCreationDate(LocalDateTime.now());
        statementRepository.save(statementEntity);
        log.info("Created statement saved in database: {}", statementEntity);
        return statementEntity.getStatementId();
    }

    /**
     * Обновление состояния заявки (или сохранение новой)
     *
     * @param statementEntity Сущность заявки
     */
    public void updateStatement(StatementEntity statementEntity) {
        if (statementEntity == null) {
            log.error("statement entity is null");
            throw new IllegalArgumentException("Statement entity cannot be null");
        }
        statementRepository.save(statementEntity);
        log.info("Updated statement saved in database: {}", statementEntity);
    }

    /**
     * Получение сущности заявки через её идентификатор
     *
     * @param statementId Идентификатор заявки
     * @return Искомая заявка
     */
    public StatementEntity getStatement(UUID statementId) {
        return statementRepository.findByStatementId(statementId).orElseThrow(() -> new EntityNotFoundException("Can't find statement with such id"));
    }

    /**
     * Обновление статуса заявки в зависимости от этапа сделки
     *
     * @param statementId       Идентификационный номер сделки
     * @param applicationStatus Текущий статус сделки
     */
    @Transactional
    public void updateStatementStatus(UUID statementId, ApplicationStatus applicationStatus) {
        StatementEntity statementEntity = getStatementForUpdate(statementId);
        statementEntity.setApplicationStatus(applicationStatus);
        log.info("Updated statement status saved in database: {}", statementEntity);
        updateStatement(statementEntity);
    }

    /**
     * Получение сущности сделки для её обновления с использованием блокировки
     *
     * @param statementId Идентификационный номер сделки
     * @return сущность сделки
     */
    public StatementEntity getStatementForUpdate(UUID statementId) {
        return statementRepository.findByStatementIdForUpdate(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find statement with such id"));
    }

    /**
     * Получение почты клиента для отправки ему уведомлений
     *
     * @param statementId Идентификационный номер сделки
     * @return Электронная почта клиента
     */
    public String getClientEmailFromStatementByStatementId(UUID statementId) {
        return statementRepository.findClientEmailByStatementId(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find statement with such id"));
    }

    /**
     * Обновление статуса заявки после того, как клиент выбрал предложение по кредиту
     *
     * @param loanOfferDto Выбранное предложение
     */
    @Transactional
    public void updateStatementWithChoosedOffer(LoanOfferDto loanOfferDto) {
        StatementEntity statementEntity = getStatementForUpdate(loanOfferDto.getStatementId());
        statementEntity.setAppliedOffer(loanOfferDto);
        statementEntity.updateStatus(ApplicationStatus.APPROVED);
        log.info("Updated statement with loan offer: {}", loanOfferDto);
        updateStatement(statementEntity);
        kafkaProducerService.send("finish-registration",
                new EmailMessage(statementEntity.getClient().getEmail(),
                        EmailTheme.FINISH_REGISTRATION,
                        statementEntity.getStatementId(),
                        "Ваша заявка предварительно одобрена, завершите оформление"));
    }

    /**
     * Подписание сделки и сохранение проверочного кода
     *
     * @param statementId Идентификационный номер сделки
     * @param sesCode     Код проверки подписи
     */
    @Transactional
    public void signStatementWithSignCode(UUID statementId, String sesCode) {
        StatementEntity statementEntity = getStatementForUpdate(statementId);
        statementEntity.setSignDate(LocalDateTime.now());
        statementEntity.setSesCode(sesCode);
        statementEntity.setApplicationStatus(ApplicationStatus.DOCUMENT_SIGNED);
        log.info("Updated signed statement: {}", statementEntity);
        updateStatement(statementEntity);
    }
}
