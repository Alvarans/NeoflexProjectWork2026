package com.example.apigateway.service;

import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Сервис, обслуживающий запросы к сервису Statement
 */
public interface StatementRestClientService {
    /**
     * Запрос на создание сделки в сервисе Statement
     * @param loanStatementRequestDto Первичная информация о клиенте
     * @return Список из 4 предложений по кредиту
     */
    ResponseEntity<List<LoanOfferDto>> createStatement(LoanStatementRequestDto loanStatementRequestDto);

    /**
     * Выбор одного из предложений по кредиту
     * @param loanOfferDto Выбранное предложение
     * @return 200
     */
    ResponseEntity<Void> selectOffer(LoanOfferDto loanOfferDto);

}
