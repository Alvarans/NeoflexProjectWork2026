package com.example.deal.service;

import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DealService {

private final ClientService clientService;
private final StatementService statementService;
private final CalculatorRestClientService calculatorRestClientService;

    public List<LoanOfferDto> makeADealStatement (LoanStatementRequestDto loanStatementRequestDto) {
        UUID clientId = clientService.createClient(loanStatementRequestDto);
        UUID statementID = statementService.createStatement(clientId);
        List<LoanOfferDto> offers = calculatorRestClientService.getOffers(loanStatementRequestDto);
        for (LoanOfferDto loanOfferDto : offers) {
            loanOfferDto.setStatementId(statementID);
        }
        return offers;
    }

}
