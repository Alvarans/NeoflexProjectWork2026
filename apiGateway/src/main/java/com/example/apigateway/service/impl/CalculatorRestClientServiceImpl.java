package com.example.apigateway.service.impl;

import com.example.api.common.dto.CreditDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.api.common.dto.ScoringDataDto;
import com.example.apigateway.service.CalculatorRestClientService;
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
public class CalculatorRestClientServiceImpl implements CalculatorRestClientService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public CalculatorRestClientServiceImpl(RestClient.Builder restClientBuilder, ObjectMapper objectMapper
            , @Value("${services.calculator.url}") String baseUrl) {
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Получение списка из 4 предложений от Calculator
     * @param loanStatementRequestDto - Информация для запроса по кредитным предложениям
     * @return - список из 4 предложений от худшего к лучшему
     */
    @Override
    public ResponseEntity<List<LoanOfferDto>> creditOffers(LoanStatementRequestDto loanStatementRequestDto) {
        return restClient.post()
                .uri("/calculator/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loanStatementRequestDto)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    /**
     * Подсчёт кредита для клиента, проводимый в Calculator
     * @param scoringDataDto - собранная информация о клиенте, кредите и сотруднике, обслуживающем его
     * @return Объект кредита, содержащий в себе основную информацию о нём + график платежей
     */
    @Override
    public ResponseEntity<CreditDto> creditCalculation(ScoringDataDto scoringDataDto) {
        return restClient.post()
                .uri("/calculator/calc")
                .contentType(MediaType.APPLICATION_JSON)
                .body(scoringDataDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("Validation on calculator credit calculation goes wrong. Validation failed");
                    log.info("Response status : {}", response.getStatusCode());
                    log.info("Response body (must be empty object): {}", response.getBody());
                    throw new IllegalArgumentException("Check your data. Validation goes wrong");
                })
                .toEntity(CreditDto.class);
    }

}
