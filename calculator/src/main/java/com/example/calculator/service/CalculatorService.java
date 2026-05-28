package com.example.calculator.service;

import com.example.api.common.dto.CreditDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.api.common.dto.ScoringDataDto;

import java.math.BigDecimal;
import java.util.List;


public interface CalculatorService {
    /**
     * Method generate all kind of credit offers
     *
     * @param requestDto - request with information about client
     * @return List of offers
     */
    List<LoanOfferDto> generateOffers(LoanStatementRequestDto requestDto);

    /**
     * Method for creating credit
     *
     * @param scoringDataDto - information about client and requested credit
     * @param scoredRate     - additional rate, calculated in scoring
     * @return credit with monthly payment schedule
     */
    CreditDto createCredit(ScoringDataDto scoringDataDto, BigDecimal scoredRate);

}
