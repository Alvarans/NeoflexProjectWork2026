package com.example.deal.service.impl;

import com.example.api.common.dto.CreditDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.api.common.dto.ScoringDataDto;
import com.example.deal.service.CalculatorRestClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import java.util.List;

/**
 * Реализация Rest client сервиса для отправления запросов в Calculator
 */
@Service
public class CalculatorRestClientServiceImpl implements CalculatorRestClientService {
    private final RestClient restClient;

    public CalculatorRestClientServiceImpl(RestClient.Builder restClientBuilder, @Value("${services.calculator.url}") String baseUrl) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Получение списка из 4 предложений от Calculator
     * @param loanStatementRequestDto - Информация для запроса по кредитным предложениям
     * @return - список из 4 предложений от худшего к лучшему
     */
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto loanStatementRequestDto) {
        return restClient.post()
                .uri("/calculator/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loanStatementRequestDto)
                .retrieve()
                .body(new ParameterizedTypeReference<List<LoanOfferDto>>() {});
    }

    /**
     * Подсчёт кредита для клиента, проводимый в Calculator
     * @param scoringDataDto - собранная информация о клиенте, кредите и сотруднике, обслуживающем его
     * @return Объект кредита, содержащий в себе основную информацию о нём + график платежей
     */
    public CreditDto calculateCredit(ScoringDataDto scoringDataDto) {
        return restClient.post()
                .uri("/calculator/calc")
                .contentType(MediaType.APPLICATION_JSON)
                .body(scoringDataDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new IllegalArgumentException("Check your data. Validation goes wrong");
                })
                .body(CreditDto.class);
    }
}
