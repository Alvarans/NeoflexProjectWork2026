package com.example.apigateway.service;

import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.apigateway.dto.DocumentCodePassRequest;
import com.example.apigateway.dto.FinishRegistrationRequestDto;
import com.example.apigateway.dto.StatementDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

/**
 * Сервис, обслуживающий взаимодействие с сервисом Deal
 */
public interface DealRestClientService {
    /**
     * Создание первичной сделки с получением кредитных предложений
     * @param loanStatementRequestDto Первичная информация по клиенту
     * @return Список из 4 кредитных предложений
     */
    ResponseEntity<List<LoanOfferDto>> createDealStatement(LoanStatementRequestDto loanStatementRequestDto);

    /**
     * Выбор одного из кредитных предложений
     * @param loanOfferDto Выбранное предложение
     * @return 200
     */
    ResponseEntity<Void> selectDealOffer(LoanOfferDto loanOfferDto);

    /**
     * Проверка подписи клиента с помощью специального кода
     * @param statementId Идентификатор пользователя
     * @param documentCodePassRequest Проверочный код
     * @return 200, если код подходит и 400, если нет
     */
    ResponseEntity<Void> documentCodePass(UUID statementId, DocumentCodePassRequest documentCodePassRequest);

    /**
     * Отправление готовых документов по кредиту клиенту
     * @param statementId Идентификатор сделки
     * @return 200 и сообщение с документами на email клиента
     */
    ResponseEntity<Void> sendDocuments(UUID statementId);

    /**
     * Запрос на подписание документов клиентом
     * @param statementId Идентификатор сделки
     * @return 200 и сообщение с запросом на подпись на email клиента
     */
    ResponseEntity<Void> signDocuments(UUID statementId);

    /**
     * Подсчёт кредита на основе полных данных по клиенту
     * @param statementId Идентификатор сделки
     * @param finishRegistrationRequestDto Дополненная информация по клиенту
     * @return 200
     */
    ResponseEntity<Void> calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto);

    /**
     * Получение полного списка сделок
     * @return Список сделок
     */
    ResponseEntity<List<StatementDto>> getAllStatements();

    /**
     * Получение информации по конкретной сделке
     * @param statementId Идентификатор сделки
     * @return Информация по сделке
     */
    ResponseEntity<StatementDto> getStatementById(UUID statementId);
}
