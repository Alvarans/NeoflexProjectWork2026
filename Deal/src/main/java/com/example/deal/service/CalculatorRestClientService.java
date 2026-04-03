package com.example.deal.service;

import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import org.springframework.core.ParameterizedTypeReference;
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
}
