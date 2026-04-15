package com.example.statement.service;

import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;

import java.util.List;

/**
 * Сервис, обслуживающий бизнес-логику statement MVP
 */
public interface StatementService {

    /**
     * Проверка данных на формирование предложений и отправление запроса на их получение
     * @param loanStatementRequestDto данные, необходимые для формирования предложений
     * @return список из 4 предложений, который возвращается к нам по цепочке calculator -> deal -> statement
     */
    List<LoanOfferDto> createOffers(LoanStatementRequestDto loanStatementRequestDto);

    /**
     * Выбор интересующего предложения из предложенных
     * @param loanOfferDto выбранное предложение
     */
    void selectOffer(LoanOfferDto loanOfferDto);
}
