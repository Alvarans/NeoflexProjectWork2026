package com.example.calculator.service;

import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatorServiceTest {
    @Mock
    private ScoringService scoringService;
    @InjectMocks
    private CalculatorService calculatorService;

    @Test
    void generateOffers() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        requestDto.setTerm(8);
        BigDecimal totalAmountWithInsur = new BigDecimal("100005");
        BigDecimal totalAmountWithoutInsur = new BigDecimal("100002");
        when(scoringService.calculateTotalAmount(any(), anyBoolean(), anyBoolean()))
                .thenReturn(totalAmountWithoutInsur, totalAmountWithoutInsur, totalAmountWithInsur, totalAmountWithoutInsur);

        BigDecimal baseRate = new BigDecimal(16);
        BigDecimal averageRate = new BigDecimal(15);
        BigDecimal smallRate = new BigDecimal(14);
        when(scoringService.calculateRate(anyBoolean(), anyBoolean()))
                .thenReturn(baseRate, baseRate, smallRate, averageRate);

        BigDecimal firstMonthyPayment = new BigDecimal("8500").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal secondMonthyPayment = new BigDecimal("8300").setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal thirdMonthyPayment = new BigDecimal("10000").setScale(2, RoundingMode.HALF_EVEN);
        when(scoringService.calculateMonthlyPayment(any(), any(), any()))
                .thenReturn(firstMonthyPayment, firstMonthyPayment, thirdMonthyPayment, secondMonthyPayment);

        List<LoanOfferDto> loanOfferDtoList = calculatorService.generateOffers(requestDto);

        assertEquals(4, loanOfferDtoList.size());
        assertEquals(8, loanOfferDtoList.get(0).getTerm());

        Mockito.verify(scoringService, Mockito.times(4)).calculateTotalAmount(Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.verify(scoringService, Mockito.times(4)).calculateRate(Mockito.anyBoolean(), Mockito.anyBoolean());
        Mockito.verify(scoringService, Mockito.times(4)).calculateMonthlyPayment(any(), any(), any());

        assertEquals(totalAmountWithoutInsur, loanOfferDtoList.get(0).getTotalAmount());
        assertEquals(totalAmountWithoutInsur, loanOfferDtoList.get(1).getTotalAmount());
        assertEquals(totalAmountWithoutInsur, loanOfferDtoList.get(2).getTotalAmount());
        assertEquals(totalAmountWithInsur, loanOfferDtoList.get(3).getTotalAmount());

        assertEquals(baseRate, loanOfferDtoList.get(0).getRate());
        assertEquals(baseRate, loanOfferDtoList.get(1).getRate());
        assertEquals(averageRate, loanOfferDtoList.get(2).getRate());
        assertEquals(smallRate, loanOfferDtoList.get(3).getRate());

        assertEquals(firstMonthyPayment, loanOfferDtoList.get(0).getMonthlyPayment());
        assertEquals(firstMonthyPayment, loanOfferDtoList.get(1).getMonthlyPayment());
        assertEquals(secondMonthyPayment, loanOfferDtoList.get(2).getMonthlyPayment());
        assertEquals(thirdMonthyPayment, loanOfferDtoList.get(3).getMonthlyPayment());

    }

    @Test
    void createCredit() {
        ScoringDataDto scoringDataDto = new ScoringDataDto();
        int term = 8;
        scoringDataDto.setTerm(term);
        scoringDataDto.setIsInsuranceEnabled(false);
        scoringDataDto.setIsSalaryClient(false);
        scoringDataDto.setAmount(new BigDecimal(300000));
        BigDecimal addRate = new BigDecimal(4);
        BigDecimal rate = new BigDecimal(16);

        when(scoringService.calculateRate(anyBoolean(), anyBoolean()))
                .thenReturn(rate);
        when(scoringService.calculateTotalAmount(scoringDataDto.getAmount(),
                scoringDataDto.getIsInsuranceEnabled(),
                scoringDataDto.getIsSalaryClient())).
                thenReturn(new BigDecimal(300000));
        when(scoringService.calculateMonthlyPayment(any(), any(), any()))
                .thenReturn(new BigDecimal(7000));

        CreditDto creditDto = calculatorService.createCredit(scoringDataDto, addRate);

        assertEquals(new BigDecimal(20), creditDto.getRate());
        assertEquals(scoringDataDto.getTerm(), creditDto.getTerm());
        assertEquals(scoringDataDto.getAmount(), creditDto.getAmount());
        assertEquals(scoringDataDto.getIsInsuranceEnabled(), creditDto.getIsInsuranceEnabled());
        assertEquals(scoringDataDto.getIsSalaryClient(), creditDto.getIsSalaryClient());

    }
}