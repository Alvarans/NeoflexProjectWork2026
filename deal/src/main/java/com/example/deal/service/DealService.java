package com.example.deal.service;

import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.deal.dto.FinishRegistrationRequestDto;

import java.util.List;
import java.util.UUID;

/**
 * Сервис, обслуживающий контроллер сделки. Является связующим звеном с другими сервисами
 */
public interface DealService {
    /**
     * Формирование данных для подсчёта кредита и дополнение информации о клиенте
     *
     * @param statementId                  идентификатор заявки
     * @param finishRegistrationRequestDto Дополнительная информация о клиенте для создания кредита
     */
    void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto);
    /**
     * Создание заявки на кредит. Предоставляет 4 предложения по кредиту
     *
     * @param loanStatementRequestDto Данные для создания заявки
     * @return Лист из 4 кредитных предложений
     */
    List<LoanOfferDto> makeADealStatement(LoanStatementRequestDto loanStatementRequestDto);
    /**
     * Выбор предложенного оффера
     *
     * @param loanOfferDto Выбранное предложение
     */
    void selectOffer(LoanOfferDto loanOfferDto);

}
