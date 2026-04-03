package com.example.deal.service;

import com.example.deal.dto.CreditDto;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.dto.ScoringDataDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class CalculatorRestClientService {
    private final RestClient restClient;

    public CalculatorRestClientService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8010")
                .build();
    }

    public List<LoanOfferDto> getOffers(LoanStatementRequestDto loanStatementRequestDto) {
        return restClient.post()
                .uri("/calculator/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loanStatementRequestDto)
                .retrieve()
                .body(new ParameterizedTypeReference<List<LoanOfferDto>>() {});
    }

    public CreditDto calculateCredit(ScoringDataDto scoringDataDto) {
        return restClient.post()
                .uri("calculator/calc")
                .contentType(MediaType.APPLICATION_JSON)
                .body(scoringDataDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    // Логируем или выбрасываем кастомное исключение,
                    // так как контроллер при ошибке вернет пустой CreditDto
                    //log.error("Ошибка при расчете кредита: {}", response.getStatusCode());
                })
                .body(CreditDto.class);
    }
}
