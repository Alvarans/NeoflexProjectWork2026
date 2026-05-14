package com.example.deal.controller;

import com.example.deal.api.DealApi;
import com.example.deal.dto.DocumentCodePassRequest;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.deal.dto.StatementDto;
import com.example.deal.service.DealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер, предоставляющий эндпоинты для взаимодействия с deal MVP
 */
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class DealController implements DealApi {
    private final DealService dealService;

    /**
     * Подсчёт кредита для клиента
     * @param statementId  (required) Идентификатор заявки клиента
     * @param finishRegistrationRequestDto  (required) Дополнительная информация о клиенте
     * @return Код успешного выполнения запроса. По заданию возвращать кредит не нужно
     */
    @Override
    public ResponseEntity<Void> calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        dealService.calculateCredit(statementId, finishRegistrationRequestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Создание заявки на кредит
     * @param loanStatementRequestDto  (required) Данные для создания заявки
     * @return Список из четырёх кредитных предложений
     */
    @Override
    public ResponseEntity<List<LoanOfferDto>> createStatement(@Validated @RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        return ResponseEntity.ok(dealService.makeADealStatement(loanStatementRequestDto));
    }

    /**
     * Проверка проверочного кода
     * @param statementId  (required) Идентификационный номер заявки
     * @param sesCode  (required) Код подтверждения
     * @return 200
     */
    @Override
    public ResponseEntity<Void> documentCodePass(UUID statementId, DocumentCodePassRequest sesCode) {
        if (dealService.documentCodePass(statementId, sesCode.getSescode()))
            return ResponseEntity.ok().build();
        else {
            log.error("Код подтверждения неверен");
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<StatementDto>> getAllStatements() {
        return ResponseEntity.ok(dealService.getAllStatements());
    }

    @Override
    public ResponseEntity<StatementDto> getStatementById(UUID statementId) {
        return ResponseEntity.ok(dealService.getStatement(statementId));
    }

    /**
     * Выбор предложения из представленных
     * @param loanOfferDto  (required) Выбранное предложение
     * @return Код успешного выполнения запроса. По заданию возвращать ничего не нужно
     */
    @Override
    public ResponseEntity<Void> selectOffer(LoanOfferDto loanOfferDto) {
        dealService.selectOffer(loanOfferDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Отправка документов клиенту
     * @param statementId  (required) Идентификационный номер заявки
     * @return 200
     */
    @Override
    public ResponseEntity<Void> sendDocuments(UUID statementId) {
        dealService.sendDocuments(statementId);
        return ResponseEntity.ok().build();
    }

    /**
     * Подписание документов клиентом
     * @param statementId  (required) Идентификационный номер заявки
     * @return 200
     */
    @Override
    public ResponseEntity<Void> signDocuments(UUID statementId) {
        dealService.signDocuments(statementId);
        return ResponseEntity.ok().build();
    }
}
