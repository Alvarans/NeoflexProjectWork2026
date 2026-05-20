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
 * Сервис, обслуживающий логику работы Gateway
 */
public interface ApiGatewayService {
    /**
     * Подсчёт кредита для клиента
     * @param statementId Идентификатор заявки (required)
     * @param finishRegistrationRequestDto  Заполненные данные о клиенте (required)
     * @return 200
     */
    ResponseEntity<Void> calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto);

    /**
     * Формирование заявки на получение кредита из сервиса Statement
     * @param loanStatementRequestDto Первичные данные о клиенте (required)
     * @return Список из 4 кредитных предложений
     */
    ResponseEntity<List<LoanOfferDto>> createStatement(LoanStatementRequestDto loanStatementRequestDto);

    /**
     * Подтверждение подписи документов с помощью специального кода
     * @param statementId Идентификатор заявки (required)
     * @param documentCodePassRequest Код подтверждения (required)
     * @return 200, если код подходит. 400, если не подходит
     */
    ResponseEntity<Void> documentCodePass(UUID statementId, DocumentCodePassRequest documentCodePassRequest);

    /**
     * Выбор одного кредитного предложения из предлагаемых в сервисе Statement
     * @param loanOfferDto Выбранное кредитное предложение (required)
     * @return 200
     */
    ResponseEntity<Void> selectOffer(LoanOfferDto loanOfferDto);

    /**
     * Отправление заполненных документов по кредиту клиенту
     * @param statementId Идентификатор заявки (required)
     * @return 200 и письмо с документами на почту клиента
     */
    ResponseEntity<Void> sendDocuments(UUID statementId);

    /**
     * Запрос на подписание документов клиентом
     * @param statementId Идентификатор сделки (required)
     * @return 200 и сообщение на почту с ссылкой на подпись
     */
    ResponseEntity<Void> signDocuments(UUID statementId);

    /**
     * Получение списка всех имеющихся в системе заявок
     * @return Список заявок
     */
    ResponseEntity<List<StatementDto>> getAllStatements();

    /**
     * Получение информации по заявке
     * @param statementId Идентификатор заявки (required)
     * @return Информация по заявке
     */
    ResponseEntity<StatementDto> getStatementById(UUID statementId);
}
