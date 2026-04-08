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

    /**
     * Формирование данных для подсчёта кредита и дополнение информации о клиенте
     *
     * @param statementId                  идентификатор заявки
     * @param finishRegistrationRequestDto Дополнительная информация о клиенте для создания кредита
     */
    public void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        //Проверка пришедших данных для заполнения
        if ((finishRegistrationRequestDto == null)||(!checkFinishRegistrationDtoFullfiling(finishRegistrationRequestDto))) {
            throw new IllegalArgumentException("Your finish registration requiest is not complete");
        }
        StatementEntity statementEntity = statementService.getStatement(statementId);
        //Наполнение данных для подсчёта с помощью заявки и доп.информации
        /*ScoringDataDto scoringDataDto = new ScoringDataDto();
        scoringDataDto.setAmount(statementEntity.getAppliedOffer().getTotalAmount());
        scoringDataDto.setTerm(statementEntity.getAppliedOffer().getTerm());
        scoringDataDto.setFirstName(statementEntity.getClient().getFirstName());
        scoringDataDto.setLastName(statementEntity.getClient().getLastName());
        scoringDataDto.setMiddleName(statementEntity.getClient().getMiddleName());
        scoringDataDto.setGender(finishRegistrationRequestDto.getGender());
        scoringDataDto.setBirthdate(statementEntity.getClient().getBirthDate());
        scoringDataDto.setPassportSeries(statementEntity.getClient().getPassport().getSeries());
        scoringDataDto.setPassportNumber(statementEntity.getClient().getPassport().getNumber());
        scoringDataDto.setPassportIssueDate(finishRegistrationRequestDto.getPassportIssueDate());
        scoringDataDto.setPassportIssueBranch(finishRegistrationRequestDto.getPassportIssueBrach());
        scoringDataDto.setMaritalStatus(finishRegistrationRequestDto.getMaritalStatus());
        scoringDataDto.setDependentAmount(finishRegistrationRequestDto.getDependentAmount());
        scoringDataDto.setEmployment(finishRegistrationRequestDto.getEmployment());
        scoringDataDto.setAccountNumber(finishRegistrationRequestDto.getAccountNumber());
        scoringDataDto.setIsInsuranceEnabled(statementEntity.getAppliedOffer().getIsInsuranceEnabled());
        scoringDataDto.setIsSalaryClient(statementEntity.getAppliedOffer().getIsSalaryClient());*/
        ScoringDataDto scoringDataDto = scoringDataMapper.toScoringDataDto(statementEntity, finishRegistrationRequestDto);
        log.info("ScoringDataDto fullfiled for credit calculation: {}", scoringDataDto);
        CreditDto creditDto = calculatorRestClientService.calculateCredit(scoringDataDto);
        log.info("Credit is calculated: {}", creditDto);
        log.info("Schedule size in DTO: {}", creditDto.getPaymentSchedule().size());
        creditService.createCredit(creditDto);
        statementEntity.updateStatus(ApplicationStatus.APPROVED);
        statementService.updateStatement(statementEntity);
        //Дополнение информации о клиенте
        log.info("Client information updated after credit calculation with additional info {}", finishRegistrationRequestDto);
        clientService.updateClientAfterCreditCalculation(statementEntity.getClient().getClientId(), finishRegistrationRequestDto);
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
     * Проверка данных для завершения регистрации на наличие
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
            log.error("Passport issue brach in finish registration request dto is empty");
            return false;
        }
        if (finishRegistrationRequestDto.getPassportIssueDate() == null) {
            log.error("Passport issue date in finish registration request dto is empty");
            return false;
        }
        return true;
    }
}
