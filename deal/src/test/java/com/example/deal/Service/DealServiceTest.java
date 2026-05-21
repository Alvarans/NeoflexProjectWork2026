package com.example.deal.Service;

import com.example.api.common.dto.*;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.Passport;
import com.example.deal.entity.ClientEntity;
import com.example.deal.entity.StatementEntity;
import com.example.deal.mapping.ScoringDataMapper;
import com.example.deal.service.KafkaProducerService;
import com.example.deal.service.impl.*;
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
class DealServiceTest {
    @Mock
    private ClientServiceImpl clientServiceImpl;
    @Mock
    private StatementServiceImpl statementServiceImpl;
    @Mock
    private CreditServiceImpl creditServiceImpl;
    @Mock
    private CalculatorRestClientServiceImpl calculatorRestClientServiceImpl;
    @Mock
    private ScoringDataMapper scoringDataMapper;
    @Mock
    private KafkaProducerService kafkaProducerService;
    @InjectMocks
    private DealServiceImpl dealServiceImpl;

    @Test
    void testMakeADealStatement_Success() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        UUID clientId = UUID.randomUUID();
        UUID statementId = UUID.randomUUID();

        when(clientServiceImpl.createClient(requestDto)).thenReturn(clientId);
        when(statementServiceImpl.createStatement(clientId)).thenReturn(statementId);

        LoanOfferDto offer1 = new LoanOfferDto();
        LoanOfferDto offer2 = new LoanOfferDto();
        List<LoanOfferDto> offers = List.of(offer1, offer2);

        when(calculatorRestClientServiceImpl.getOffers(requestDto)).thenReturn(offers);

        List<LoanOfferDto> result = dealServiceImpl.makeADealStatement(requestDto);

        assertEquals(2, result.size());
        assertEquals(statementId, result.get(0).getStatementId());
        assertEquals(statementId, result.get(1).getStatementId());

        verify(clientServiceImpl).createClient(requestDto);
        verify(statementServiceImpl).createStatement(clientId);
        verify(calculatorRestClientServiceImpl).getOffers(requestDto);
    }

    @Test
    void testSelectOffer_Success() {
        LoanOfferDto loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(UUID.randomUUID());
        loanOfferDto.setTerm(48);
        loanOfferDto.setTotalAmount(BigDecimal.valueOf(130000));
        loanOfferDto.setRequestedAmount(BigDecimal.valueOf(100000));
        loanOfferDto.setIsSalaryClient(Boolean.TRUE);
        loanOfferDto.setIsInsuranceEnabled(Boolean.TRUE);
        loanOfferDto.setMonthlyPayment(BigDecimal.valueOf(10000));
        loanOfferDto.setRate(BigDecimal.valueOf(13));
        dealServiceImpl.selectOffer(loanOfferDto);
        verify(statementServiceImpl).updateStatementWithChoosedOffer(loanOfferDto);
    }

    @Test
    void testCalculateCredit_Success() {
        UUID statementId = UUID.randomUUID();
        EmploymentDto employmentDto = new EmploymentDto();
        employmentDto.setEmployerINN(UUID.randomUUID().toString());
        employmentDto.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employmentDto.setPosition(Positions.MID_MANAGER);
        employmentDto.setSalary(BigDecimal.valueOf(55000));
        employmentDto.setWorkExperienceTotal(20);
        employmentDto.setWorkExperienceCurrent(6);
        FinishRegistrationRequestDto finishDto = new FinishRegistrationRequestDto();
        finishDto.setGender(Genders.MALE);
        finishDto.setPassportIssueDate(LocalDate.of(2020,1,1));
        finishDto.setPassportIssueBrach("Branch1");
        finishDto.setAccountNumber(UUID.randomUUID().toString());
        finishDto.setMaritalStatus(MaritalStatus.SINGLE);
        finishDto.setEmployment(employmentDto);
        finishDto.setDependentAmount(30000);

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

        when(statementServiceImpl.getStatement(statementId)).thenReturn(statement);

        CreditDto creditDto = new CreditDto();
        creditDto.setPaymentSchedule(List.of());
        when(calculatorRestClientServiceImpl.calculateCredit(any())).thenReturn(creditDto);

        dealServiceImpl.calculateCredit(statementId, finishDto);

        verify(statementServiceImpl).getStatement(statementId);
        verify(calculatorRestClientServiceImpl).calculateCredit(any());
        verify(creditServiceImpl).createCredit(creditDto);
        verify(statementServiceImpl).updateStatement(statement);
        verify(clientServiceImpl).updateClientAfterCreditCalculation(client.getClientId(), finishDto);
    }
}
