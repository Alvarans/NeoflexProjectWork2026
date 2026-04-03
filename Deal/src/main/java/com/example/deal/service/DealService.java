package com.example.deal.service;

import com.example.deal.dto.*;
import com.example.deal.entity.StatementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DealService {

private final ClientService clientService;
private final StatementService statementService;
private final CreditService creditService;
private final CalculatorRestClientService calculatorRestClientService;

    public void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        StatementEntity statementEntity = statementService.getStatement(statementId);
        ScoringDataDto scoringDataDto = new ScoringDataDto();
        scoringDataDto.setAmount(statementEntity.getAppliedOffer().getTotalAmount());
        scoringDataDto.setTerm(statementEntity.getAppliedOffer().getTerm());
        scoringDataDto.setFirstName(statementEntity.getClient().getFirstName());
        scoringDataDto.setLastName(statementEntity.getClient().getLastName());
        scoringDataDto.setMiddleName(statementEntity.getClient().getMiddleName());
        //Update to client
        scoringDataDto.setGender(finishRegistrationRequestDto.getGender());
        scoringDataDto.setBirthdate(statementEntity.getClient().getBirthDate());
        scoringDataDto.setPassportSeries(statementEntity.getClient().getPassport().getSeries());
        scoringDataDto.setPassportNumber(statementEntity.getClient().getPassport().getNumber());
        //Update to client
        scoringDataDto.setPassportIssueDate(finishRegistrationRequestDto.getPassportIssueDate());
        scoringDataDto.setPassportIssueBranch(finishRegistrationRequestDto.getPassportIssueBrach());
        scoringDataDto.setMaritalStatus(finishRegistrationRequestDto.getMaritalStatus());
        //
        scoringDataDto.setDependentAmount(finishRegistrationRequestDto.getDependentAmount());
        //Update to client
        scoringDataDto.setEmployment(finishRegistrationRequestDto.getEmployment());
        scoringDataDto.setAccountNumber(finishRegistrationRequestDto.getAccountNumber());
        //
        scoringDataDto.setIsInsuranceEnabled(statementEntity.getAppliedOffer().getIsInsuranceEnabled());
        scoringDataDto.setIsSalaryClient(statementEntity.getAppliedOffer().getIsSalaryClient());

        CreditDto creditDto = calculatorRestClientService.calculateCredit(scoringDataDto);
        creditService.createCredit(creditDto);
        statementEntity.updateStatus(ApplicationStatus.APPROVED);
        statementService.updateStatement(statementEntity);
        clientService.updateClientAfterCreditCalculation(statementEntity.getClient().getClientId(), finishRegistrationRequestDto);
    }

    public List<LoanOfferDto> makeADealStatement (LoanStatementRequestDto loanStatementRequestDto) {
        UUID clientId = clientService.createClient(loanStatementRequestDto);
        UUID statementID = statementService.createStatement(clientId);
        List<LoanOfferDto> offers = calculatorRestClientService.getOffers(loanStatementRequestDto);
        for (LoanOfferDto loanOfferDto : offers) {
            loanOfferDto.setStatementId(statementID);
        }
        return offers;
    }

    public void selectOffer(LoanOfferDto loanOfferDto){
        statementService.updateStatementWithChoosedOffer(loanOfferDto);
    }

}
