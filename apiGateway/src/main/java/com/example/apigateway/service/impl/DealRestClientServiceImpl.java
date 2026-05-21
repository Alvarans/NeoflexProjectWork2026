package com.example.apigateway.service.impl;

import com.example.api.common.dto.CreditDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.apigateway.dto.DocumentCodePassRequest;
import com.example.apigateway.dto.FinishRegistrationRequestDto;
import com.example.apigateway.dto.StatementDto;
import com.example.apigateway.service.DealRestClientService;
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
import java.util.UUID;

@Service
@Slf4j
public class DealRestClientServiceImpl implements DealRestClientService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private static final String RESPONSE_CODE = "Response code : {}";

    public DealRestClientServiceImpl(RestClient.Builder restClientBuilder, ObjectMapper objectMapper
            , @Value("${services.deal.url}") String baseUrl) {
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public ResponseEntity<List<LoanOfferDto>> createDealStatement(LoanStatementRequestDto loanStatementRequestDto) {
        return restClient.post()
                .uri("deal/statement")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loanStatementRequestDto)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<LoanOfferDto>>() {});
    }

    @Override
    public ResponseEntity<Void> selectDealOffer(LoanOfferDto loanOfferDto) {
        return restClient.post()
                .uri("deal/offer/select")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loanOfferDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("Error was caused in selection offer. Validation failed. Request body: {}", loanOfferDto);
                    CreditDto errorDto = objectMapper.readValue(response.getBody(), CreditDto.class);
                    log.info(RESPONSE_CODE, response.getStatusCode());
                    log.info("Response body : {}", errorDto.toString());
                    throw new IllegalArgumentException("Ошибка валидации данных: " + errorDto.toString());
                })
                .toEntity(Void.class);
    }

    @Override
    public ResponseEntity<Void> documentCodePass(UUID statementId, DocumentCodePassRequest documentCodePassRequest) {
        return restClient.post()
                .uri("deal/document/{statementId}/code", statementId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(documentCodePassRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("Code verification failed. Code is wrong. Rejected. Statement ID: {}", statementId);
                    log.info(RESPONSE_CODE, response.getStatusCode());
                    throw new IllegalArgumentException("Ошибка подтверждения кода");
                })
                .toBodilessEntity();
    }

    @Override
    public ResponseEntity<Void> sendDocuments(UUID statementId) {
        return restClient.post()
                .uri("deal/document/{statementId}/send", statementId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("Failed to send documents. StatementID: {}", statementId);
                    log.info(RESPONSE_CODE, response.getStatusCode());
                    throw new IllegalArgumentException("Ошибка отправки документов");
                })
                .toBodilessEntity();
    }

    @Override
    public ResponseEntity<Void> signDocuments(UUID statementId) {
        return restClient.post()
                .uri("deal/document/{statementId}/sign", statementId)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("Failed to send documents on sign. StatementID: {}", statementId);
                    log.info(RESPONSE_CODE, response.getStatusCode());
                    throw new IllegalArgumentException("Ошибка отправки документов на подпись");
                })
                .toBodilessEntity();
    }

    @Override
    public ResponseEntity<Void> calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        return restClient.post()
                .uri("/deal/calculate/{statementId}", statementId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(finishRegistrationRequestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("Failed to calculate credit. StatementID: {}", statementId);
                    log.info("Request body: {}", finishRegistrationRequestDto);
                    log.info(RESPONSE_CODE, response.getStatusCode());
                    log.info("Response body : {}", response.getBody());
                    throw new IllegalArgumentException("Check your data. Validation goes wrong");
                })
                .toBodilessEntity();
    }

    @Override
    public ResponseEntity<List<StatementDto>> getAllStatements() {
        return restClient.get()
                .uri("/deal/admin/statement")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});
    }

    @Override
    public ResponseEntity<StatementDto> getStatementById(UUID statementId) {
        return restClient.get()
                .uri("/deal/admin/statement/{statementId}", statementId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("Failed to get statement - statement not found. StatementID: {}", statementId);
                    log.info(RESPONSE_CODE, response.getStatusCode());
                    throw new IllegalArgumentException("Check your statement Id. Statement not found");
                })
                .toEntity(new ParameterizedTypeReference<>() {});
    }
}
