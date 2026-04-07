package com.example.deal.controller;

import com.example.api.common.dto.models.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.deal.api.DealApi;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.service.DealService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
     * Выбор предложения из представленных
     * @param loanOfferDto  (required) Выбранное предложение
     * @return Код успешного выполнения запроса. По заданию возвращать ничего не нужно
     */
    @Override
    public ResponseEntity<Void> selectOffer(LoanOfferDto loanOfferDto) {
        dealService.selectOffer(loanOfferDto);
        return ResponseEntity.ok().build();
    }
}
