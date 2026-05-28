package com.example.statement.service;

import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Сервис, позволяющий отправлять запросы в deal MVP
 */
@FeignClient(name = "deal-client",url = "${services.deal.url}")
public interface DealClientService {
    /**
     * Получение списка из 4 кредитных предложений от deal MVP
     * @param loanStatementRequestDto данные, необходимые для формирования предложений
     * @return список из 4 предложений, варьирующихся от показателей "кредитный клиент" и "страховка"
     */
    @PostMapping("/deal/statement")
    List<LoanOfferDto> createOffers(@RequestBody LoanStatementRequestDto loanStatementRequestDto);

    /**
     * Выбор интересующего предложения клиентом
     * @param loanOfferDto выбранное предложение
     */
    @PostMapping("/deal/offer/select")
    void selectOffer(@RequestBody LoanOfferDto loanOfferDto);
}
