package com.example.apigateway.service.impl;

import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.apigateway.dto.DocumentCodePassRequest;
import com.example.apigateway.dto.FinishRegistrationRequestDto;
import com.example.apigateway.dto.StatementDto;
import com.example.apigateway.service.ApiGatewayService;
import com.example.apigateway.service.CalculatorRestClientService;
import com.example.apigateway.service.DealRestClientService;
import com.example.apigateway.service.StatementRestClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiGatewayServiceImpl implements ApiGatewayService {

    private final CalculatorRestClientService calculatorRestClientService;
    private final DealRestClientService dealRestClientService;
    private final StatementRestClientService statementRestClientService;

    private static final String RESPONSE_STATUS_LOG = "Response status : {}";
    private static final String RESPONSE_BODY = "Response body : {}";
    @Override
    public ResponseEntity<Void> calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        log.info("POST request come on /deal/calculate/{statementId} with {}", statementId);
        log.info("Body for calculation: {}", finishRegistrationRequestDto);
        ResponseEntity<Void> response = dealRestClientService.calculateCredit(statementId, finishRegistrationRequestDto);
        log.info(RESPONSE_STATUS_LOG, response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<List<LoanOfferDto>> createStatement(LoanStatementRequestDto loanStatementRequestDto) {
        log.info("POST request come on /statement");
        log.info("Body for statement creating: {}", loanStatementRequestDto);
        ResponseEntity<List<LoanOfferDto>> response = statementRestClientService.createStatement(loanStatementRequestDto);
        log.info(RESPONSE_STATUS_LOG, response.getStatusCode());
        log.info(RESPONSE_BODY, response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<Void> documentCodePass(UUID statementId, DocumentCodePassRequest documentCodePassRequest) {
        log.info("POST  request come on /deal/document/{statementId}/code with {}", statementId);
        log.info("Body with code: {}", documentCodePassRequest);
        ResponseEntity<Void> response = dealRestClientService.documentCodePass(statementId, documentCodePassRequest);
        log.info(RESPONSE_STATUS_LOG, response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<Void> selectOffer(LoanOfferDto loanOfferDto) {
        log.info("POST request come on /statement/offer");
        log.info("Body with selected offer: {}", loanOfferDto);
        ResponseEntity<Void> response = statementRestClientService.selectOffer(loanOfferDto);
        log.info(RESPONSE_STATUS_LOG, response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<Void> sendDocuments(UUID statementId) {
        log.info("POST request come on /deal/document/{statementId}/send with {}", statementId);
        ResponseEntity<Void> response = dealRestClientService.sendDocuments(statementId);
        log.info(RESPONSE_STATUS_LOG, response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<Void> signDocuments(UUID statementId) {
        log.info("POST request come on /deal/document/{statementId}/sign with {}", statementId);
        ResponseEntity<Void> response = dealRestClientService.signDocuments(statementId);
        log.info(RESPONSE_STATUS_LOG, response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<List<StatementDto>> getAllStatements() {
        log.info("GET request come on /deal/admin/statement");
        ResponseEntity<List<StatementDto>> response = dealRestClientService.getAllStatements();
        log.info(RESPONSE_STATUS_LOG, response.getStatusCode());
        log.info(RESPONSE_BODY, response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<StatementDto> getStatementById(UUID statementId) {
        log.info("GET request come on /deal/admin/statement/{statementId} with {}", statementId);
        ResponseEntity<StatementDto> response = dealRestClientService.getStatementById(statementId);
        log.info(RESPONSE_STATUS_LOG, response.getStatusCode());
        log.info(RESPONSE_BODY, response.getBody());
        return response;
    }
}
