package com.example.deal.service;

import com.example.deal.dto.*;
import com.example.deal.entity.StatementEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Сервис, обслуживающий контроллер сделки. Является связующим звеном с другими сервисами
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DealService {

    private final ClientService clientService;
    private final StatementService statementService;
    private final CreditService creditService;
    private final CalculatorRestClientService calculatorRestClientService;

    /**
     * Формирование данных для подсчёта кредита и дополнение информации о клиенте
     *
     * @param statementId                  идентификатор заявки
     * @param finishRegistrationRequestDto Дополнительная информация о клиенте для создания кредита
     */
    public void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        //Наполнение данных для подсчёта с помощью заявки и доп.информации
        StatementEntity statementEntity = statementService.getStatement(statementId);
        ScoringDataDto scoringDataDto = new ScoringDataDto();
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
        scoringDataDto.setIsSalaryClient(statementEntity.getAppliedOffer().getIsSalaryClient());
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

}
