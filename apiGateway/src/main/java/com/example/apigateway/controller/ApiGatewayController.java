package com.example.apigateway.controller;

import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.apigateway.api.GatewayApi;
import com.example.apigateway.dto.DocumentCodePassRequest;
import com.example.apigateway.dto.FinishRegistrationRequestDto;
import com.example.apigateway.dto.StatementDto;
import com.example.apigateway.service.ApiGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Gateway для кредитного конвейера
 */
@RestController
@RequiredArgsConstructor
@Validated
public class ApiGatewayController implements GatewayApi {
    private final ApiGatewayService apiGatewayService;

    /**
     * Подсчёт кредита для клиента
     * @param statementId Идентификатор заявки (required)
     * @param finishRegistrationRequestDto  Заполненные данные о клиенте (required)
     * @return 200
     */
    @Override
    public ResponseEntity<Void> calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        return apiGatewayService.calculateCredit(statementId, finishRegistrationRequestDto);
    }

    /**
     * Формирование заявки на получение кредита из сервиса Statement
     * @param loanStatementRequestDto Первичные данные о клиенте (required)
     * @return Список из 4 кредитных предложений
     */
    @Override
    public ResponseEntity<List<LoanOfferDto>> createStatement(LoanStatementRequestDto loanStatementRequestDto) {
        return apiGatewayService.createStatement(loanStatementRequestDto);
    }

    /**
     * Подтверждение подписи документов с помощью специального кода
     * @param statementId Идентификатор заявки (required)
     * @param documentCodePassRequest Код подтверждения (required)
     * @return 200, если код подходит. 400, если не подходит
     */
    @Override
    public ResponseEntity<Void> documentCodePass(UUID statementId, DocumentCodePassRequest documentCodePassRequest) {
        return apiGatewayService.documentCodePass(statementId, documentCodePassRequest);
    }

    /**
     * Получение списка всех имеющихся в системе заявок
     * @return Список заявок
     */
    @Override
    public ResponseEntity<List<StatementDto>> getAllStatements() {
        return apiGatewayService.getAllStatements();
    }

    /**
     * Получение информации по заявке
     * @param statementId Идентификатор заявки (required)
     * @return Информация по заявке
     */
    @Override
    public ResponseEntity<StatementDto> getStatementById(UUID statementId) {
        return apiGatewayService.getStatementById(statementId);
    }

    /**
     * Выбор одного кредитного предложения из предлагаемых в сервисе Statement
     * @param loanOfferDto Выбранное кредитное предложение (required)
     * @return 200
     */
    @Override
    public ResponseEntity<Void> selectOffer(LoanOfferDto loanOfferDto) {
        return apiGatewayService.selectOffer(loanOfferDto);
    }

    /**
     * Отправление заполненных документов по кредиту клиенту
     * @param statementId Идентификатор заявки (required)
     * @return 200 и письмо с документами на почту клиента
     */
    @Override
    public ResponseEntity<Void> sendDocuments(UUID statementId) {
        return apiGatewayService.sendDocuments(statementId);
    }

    /**
     * Запрос на подписание документов клиентом
     * @param statementId Идентификатор сделки (required)
     * @return 200 и сообщение на почту с ссылкой на подпись
     */
    @Override
    public ResponseEntity<Void> signDocuments(UUID statementId) {
        return apiGatewayService.signDocuments(statementId);
    }
}
