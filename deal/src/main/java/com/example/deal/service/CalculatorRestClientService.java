package com.example.deal.service;

import com.example.api.common.dto.CreditDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.api.common.dto.ScoringDataDto;

import java.util.List;

/**
 * Rest client сервис для отправления запросов в Calculator
 */
public interface CalculatorRestClientService {
    /**
     * Получение списка из 4 предложений от Calculator
     * @param loanStatementRequestDto - Информация для запроса по кредитным предложениям
     * @return - список из 4 предложений от худшего к лучшему
     */
    List<LoanOfferDto> getOffers(LoanStatementRequestDto loanStatementRequestDto);

    /**
     * Подсчёт кредита для клиента, проводимый в Calculator
     * @param scoringDataDto - собранная информация о клиенте, кредите и сотруднике, обслуживающем его
     * @return Объект кредита, содержащий в себе основную информацию о нём + график платежей
     */
    CreditDto calculateCredit(ScoringDataDto scoringDataDto);
}
