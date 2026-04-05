package com.example.deal.Service;

import com.example.deal.dto.*;
import com.example.deal.entity.ClientEntity;
import com.example.deal.entity.StatementEntity;
import com.example.deal.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DealServiceTest {
    @Mock
    private ClientService clientService;

    @Mock
    private StatementService statementService;

    @Mock
    private CreditService creditService;

    @Mock
    private CalculatorRestClientService calculatorRestClientService;

    @InjectMocks
    private DealService dealService;

    @Test
    void testMakeADealStatement_Success() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        UUID clientId = UUID.randomUUID();
        UUID statementId = UUID.randomUUID();

        when(clientService.createClient(requestDto)).thenReturn(clientId);
        when(statementService.createStatement(clientId)).thenReturn(statementId);

        LoanOfferDto offer1 = new LoanOfferDto();
        LoanOfferDto offer2 = new LoanOfferDto();
        List<LoanOfferDto> offers = List.of(offer1, offer2);

        when(calculatorRestClientService.getOffers(requestDto)).thenReturn(offers);

        List<LoanOfferDto> result = dealService.makeADealStatement(requestDto);

        assertEquals(2, result.size());
        assertEquals(statementId, result.get(0).getStatementId());
        assertEquals(statementId, result.get(1).getStatementId());

        verify(clientService).createClient(requestDto);
        verify(statementService).createStatement(clientId);
        verify(calculatorRestClientService).getOffers(requestDto);
    }

    @Test
    void testSelectOffer_Success() {
        LoanOfferDto loanOfferDto = new LoanOfferDto();
        dealService.selectOffer(loanOfferDto);
        verify(statementService).updateStatementWithChoosedOffer(loanOfferDto);
    }

    @Test
    void testCalculateCredit_Success() {
        UUID statementId = UUID.randomUUID();
        FinishRegistrationRequestDto finishDto = new FinishRegistrationRequestDto();
        finishDto.setGender(Genders.MALE);
        finishDto.setPassportIssueDate(LocalDate.of(2020,1,1));
        finishDto.setPassportIssueBrach("Branch1");

        ClientEntity client = new ClientEntity();
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setMiddleName("M");
        client.setBirthDate(LocalDate.of(1990,1,1));
        Passport passport = new Passport();
        passport.setSeries("1234");
        passport.setNumber("567890");
        client.setPassport(passport);

        StatementEntity statement = new StatementEntity();
        statement.setClient(client);
        LoanOfferDto offer = new LoanOfferDto();
        offer.setTotalAmount(BigDecimal.valueOf(30000));
        offer.setTerm(12);
        offer.setIsInsuranceEnabled(true);
        offer.setIsSalaryClient(true);
        statement.setAppliedOffer(offer);

        when(statementService.getStatement(statementId)).thenReturn(statement);

        CreditDto creditDto = new CreditDto();
        creditDto.setPaymentSchedule(List.of());
        when(calculatorRestClientService.calculateCredit(any())).thenReturn(creditDto);

        dealService.calculateCredit(statementId, finishDto);

        verify(statementService).getStatement(statementId);
        verify(calculatorRestClientService).calculateCredit(any());
        verify(creditService).createCredit(creditDto);
        verify(statementService).updateStatement(statement);
        verify(clientService).updateClientAfterCreditCalculation(client.getClientId(), finishDto);
    }
}
