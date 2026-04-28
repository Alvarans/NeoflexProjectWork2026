package com.example.deal.service.impl;

import com.example.api.common.dto.CreditDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.api.common.dto.ScoringDataDto;
import com.example.deal.dto.ApplicationStatus;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.entity.StatementEntity;
import com.example.deal.mapping.ScoringDataMapper;
import com.example.deal.service.*;
import com.example.dossier.dto.EmailMessage;
import com.example.dossier.dto.EmailTheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Сервис, реализующий бизнес-логику контроллера сделки.
 * Является связующим звеном с другими сервисами
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DealServiceImpl implements DealService {

    private final ClientService clientService;
    private final StatementService statementService;
    private final CreditService creditService;
    private final CalculatorRestClientService calculatorRestClientService;
    private final ScoringDataMapper scoringDataMapper;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Формирование данных для подсчёта кредита и дополнение информации о клиенте
     *
     * @param statementId                  идентификатор заявки
     * @param finishRegistrationRequestDto Дополнительная информация о клиенте для создания кредита
     */
    public void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        //Проверка пришедших данных для заполнения
        if ((finishRegistrationRequestDto == null) || (!checkFinishRegistrationDtoFullfiling(finishRegistrationRequestDto))) {
            throw new IllegalArgumentException("Your finish registration requiest is not complete");
        }
        StatementEntity statementEntity = statementService.getStatement(statementId);
        //Наполнение данных для подсчёта с помощью заявки и доп.информации
        ScoringDataDto scoringDataDto = scoringDataMapper.toScoringDataDto(statementEntity, finishRegistrationRequestDto);
        log.info("ScoringDataDto fulfilled for credit calculation: {}", scoringDataDto);
        CreditDto creditDto = calculatorRestClientService.calculateCredit(scoringDataDto);
        log.info("Credit is calculated: {}", creditDto);
        log.info("Schedule size in DTO: {}", creditDto.getPaymentSchedule().size());
        creditService.createCredit(creditDto);
        statementEntity.updateStatus(ApplicationStatus.APPROVED);
        statementService.updateStatement(statementEntity);
        //Дополнение информации о клиенте
        log.info("Client information updated after credit calculation with additional info {}", finishRegistrationRequestDto);
        clientService.updateClientAfterCreditCalculation(statementEntity.getClient().getClientId(), finishRegistrationRequestDto);
        log.info("Sending message about creating documents");
        kafkaProducerService.send("create-documents",
                makeEmail(statementId, statementEntity.getClient().getEmail(), EmailTheme.CREATE_DOCUMENTS));
        log.info("Status updating to prepare_documents");
        statementService.updateStatementStatus(statementId, ApplicationStatus.PREPARE_DOCUMENTS);
    }

    /**
     * Создание заявки на кредит. Предоставляет 4 предложения по кредиту
     *
     * @param loanStatementRequestDto Данные для создания заявки
     * @return Лист из 4 кредитных предложений
     */
    public List<LoanOfferDto> makeADealStatement(LoanStatementRequestDto loanStatementRequestDto) {
        log.info("Creating a new client");
        UUID clientId = clientService.createClient(loanStatementRequestDto);
        log.info("Creating a new statement");
        UUID statementID = statementService.createStatement(clientId);
        List<LoanOfferDto> offers = calculatorRestClientService.getOffers(loanStatementRequestDto);

        for (LoanOfferDto loanOfferDto : offers) {
            loanOfferDto.setStatementId(statementID);
        }
        log.info("List of offers created");
        return offers;
    }

    /**
     * Выбор предложенного оффера
     *
     * @param loanOfferDto Выбранное предложение
     */
    public void selectOffer(LoanOfferDto loanOfferDto) {
        statementService.updateStatementWithChoosedOffer(loanOfferDto);
    }

    /**
     * Отправка документов на заполнение
     *
     * @param statementId Идентификатор заявки
     */
    public void sendDocuments(UUID statementId) {
        statementService.updateStatementStatus(statementId, ApplicationStatus.DOCUMENT_CREATED);
        String clientEmail = statementService.getClientEmailFromStatementByStatementId(statementId);
        kafkaProducerService.send("send-documents",
                makeEmail(statementId, clientEmail, EmailTheme.SEND_DOCUMENTS));
    }

    /**
     * Отправка документов на подпись
     *
     * @param statementId Идентификатор заявки
     */
    public void signDocuments(UUID statementId) {
        String clientEmail = statementService.getClientEmailFromStatementByStatementId(statementId);
        String codeImitation = UUID.randomUUID().toString();
        statementService.signStatementWithSignCode(statementId, codeImitation);
        EmailMessage emailMessage = makeEmail(statementId, clientEmail, EmailTheme.SEND_SES);
        emailMessage.setText(codeImitation);
        kafkaProducerService.send("send-ses", emailMessage);
    }

    /**
     * Проверка подписи
     *
     * @param statementId Идентификатор заявки
     * @param code        проверочный код
     * @return true, если проверка прошла успешно и false в ином случае
     */
    public boolean documentCodePass(UUID statementId, String code) {
        StatementEntity statementEntity = statementService.getStatement(statementId);
        if (statementEntity.getSesCode().equals(code)) {
            statementService.updateStatementStatus(statementId, ApplicationStatus.CREDIT_ISSUED);
            kafkaProducerService.send("credit-issued", makeEmail(statementId, statementEntity.getClient().getEmail(), EmailTheme.CREDIT_ISSUED));
            return true;
        } else {
            log.info("Document code passed to the client is incorrect");
            //Вообще, отказ от сделки должен происходить иначе
            kafkaProducerService.send("statement-denied", makeEmail(statementId, statementEntity.getClient().getEmail(), EmailTheme.STATEMENT_DENIED));
            return false;
        }
    }

    /**
     * Проверка данных для завершения регистрации на наличие
     *
     * @param finishRegistrationRequestDto Данные для завершения регистрации
     * @return true, если все данные имеются, или false, если какое-либо поле == null
     */
    private Boolean checkFinishRegistrationDtoFullfiling(FinishRegistrationRequestDto finishRegistrationRequestDto) {
        if (finishRegistrationRequestDto.getGender() == null) {
            log.error("Gender in finish registration request dto is empty");
            return false;
        }
        if (finishRegistrationRequestDto.getMaritalStatus() == null) {
            log.error("Marital status in finish registration request dto is empty");
            return false;
        }
        if (finishRegistrationRequestDto.getDependentAmount() == null) {
            log.error("Dependent amount in finish registration request dto is empty");
            return false;
        }
        if (finishRegistrationRequestDto.getEmployment() == null) {
            log.error("Employment in finish registration request dto is empty");
            return false;
        }
        if (finishRegistrationRequestDto.getAccountNumber() == null) {
            log.error("Account number in finish registration request dto is empty");
            return false;
        }
        if (finishRegistrationRequestDto.getPassportIssueBrach() == null) {
            log.error("Passport issue branch in finish registration request dto is empty");
            return false;
        }
        if (finishRegistrationRequestDto.getPassportIssueDate() == null) {
            log.error("Passport issue date in finish registration request dto is empty");
            return false;
        }
        return true;
    }

    /**
     * Формирование сообщения для отправления
     *
     * @param statementId Идентификатор заявки
     * @param email       Электронная почта клиента
     * @param emailTheme  Тема уведомления
     * @return Сформированное сообщение для отправки
     */
    private EmailMessage makeEmail(UUID statementId, String email, EmailTheme emailTheme) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress(email);
        emailMessage.setStatementId(statementId);
        emailMessage.setTheme(emailTheme);
        return emailMessage;
    }
}
