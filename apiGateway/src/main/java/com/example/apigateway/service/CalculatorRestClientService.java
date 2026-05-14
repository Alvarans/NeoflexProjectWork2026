package com.example.apigateway.service;

import com.example.api.common.dto.CreditDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.api.common.dto.ScoringDataDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Сервис, обслуживающий взаимодействие с сервисом Calculator
 */
public interface CalculatorRestClientService {

    /**
     * Подсчёт кредита для клиента
     * @param scoringDataDto Полная информация о клиенте
     * @return Данные по подсчитанному кредиту
     */
    ResponseEntity<CreditDto> creditCalculation(ScoringDataDto scoringDataDto);

    /**
     * Получение кредитных предложений
     * @param loanStatementRequestDto Первичная информация о клиенте
     * @return Список из четырех кредитных предложений
     */
    ResponseEntity<List<LoanOfferDto>> creditOffers(LoanStatementRequestDto loanStatementRequestDto);

}
