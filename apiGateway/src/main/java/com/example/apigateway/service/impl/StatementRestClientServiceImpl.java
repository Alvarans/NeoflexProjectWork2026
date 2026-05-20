package com.example.apigateway.service.impl;

import com.example.api.common.dto.CreditDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.apigateway.service.StatementRestClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@Slf4j
public class StatementRestClientServiceImpl implements StatementRestClientService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public StatementRestClientServiceImpl(RestClient.Builder restClientBuilder, ObjectMapper objectMapper
            , @Value("${services.statement.url}") String baseUrl) {
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }
    @Override
    public ResponseEntity<List<LoanOfferDto>> createStatement(LoanStatementRequestDto loanStatementRequestDto) {
        return restClient.post()
                .uri("/statement")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loanStatementRequestDto)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<LoanOfferDto>>() {});
    }


    @Override
    public ResponseEntity<Void> selectOffer(LoanOfferDto loanOfferDto) {
        return restClient.post()
                .uri("/statement/offer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loanOfferDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("Error occurred while calling statement. Selected offer is {}", loanOfferDto);
                    log.info("Response status : {}", response.getStatusCode());
                    CreditDto errorDto = objectMapper.readValue(response.getBody(), CreditDto.class);
                    log.info("Response body : {}", errorDto);
                    throw new IllegalArgumentException("Ошибка валидации данных: " + errorDto.toString());
                })
                .toEntity(Void.class);
    }
}


