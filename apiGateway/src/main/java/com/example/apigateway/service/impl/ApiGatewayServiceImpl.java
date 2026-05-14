package com.example.apigateway.service.impl;

import com.example.api.common.dto.CreditDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.api.common.dto.ScoringDataDto;
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

    @Override
    public ResponseEntity<Void> calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        log.info("POST request come on /deal/calculate/{statementId} with {}", statementId);
        log.info("Body: {}", finishRegistrationRequestDto);
        ResponseEntity<Void> response = dealRestClientService.calculateCredit(statementId, finishRegistrationRequestDto);
        log.info("Response status : {}", response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<List<LoanOfferDto>> createDealStatement(LoanStatementRequestDto loanStatementRequestDto) {
        log.info("POST request come on /deal/statement");
        log.info("Body: {}", loanStatementRequestDto);
        ResponseEntity<List<LoanOfferDto>> response = dealRestClientService.createDealStatement(loanStatementRequestDto);
        log.info("Response status : {}", response.getStatusCode());
        log.info("Response body : {}", response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<List<LoanOfferDto>> createStatement(LoanStatementRequestDto loanStatementRequestDto) {
        log.info("POST request come on /statement");
        log.info("Body: {}", loanStatementRequestDto);
        ResponseEntity<List<LoanOfferDto>> response = statementRestClientService.createStatement(loanStatementRequestDto);
        log.info("Response status : {}", response.getStatusCode());
        log.info("Response body : {}", response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<CreditDto> creditCalculation(ScoringDataDto scoringDataDto) {
        log.info("POST request come on /calculator/calc");
        log.info("Body: {}", scoringDataDto);
        ResponseEntity<CreditDto> response = calculatorRestClientService.creditCalculation(scoringDataDto);
        log.info("Response status : {}", response.getStatusCode());
        log.info("Response body : {}", response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<List<LoanOfferDto>> getCreditOffers(LoanStatementRequestDto loanStatementRequestDto) {
        log.info("POST request come on /calculator/offers");
        log.info("Body: {}", loanStatementRequestDto);
        ResponseEntity<List<LoanOfferDto>> response = calculatorRestClientService.creditOffers(loanStatementRequestDto);
        log.info("Response status : {}", response.getStatusCode());
        log.info("Response body : {}", response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<Void> documentCodePass(UUID statementId, DocumentCodePassRequest documentCodePassRequest) {
        log.info("POST  request come on /deal/document/{statementId}/code with {}", statementId);
        log.info("Body with code: {}", documentCodePassRequest);
        ResponseEntity<Void> response = dealRestClientService.documentCodePass(statementId, documentCodePassRequest);
        log.info("Response status : {}", response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<Void> selectDealOffer(LoanOfferDto loanOfferDto) {
        log.info("POST request come on /deal/offer/select");
        log.info("Body: {}", loanOfferDto);
        ResponseEntity<Void> response = dealRestClientService.selectDealOffer(loanOfferDto);
        log.info("Response status : {}", response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<Void> selectOffer(LoanOfferDto loanOfferDto) {
        log.info("POST request come on /statement/offer");
        log.info("Body: {}", loanOfferDto);
        ResponseEntity<Void> response = statementRestClientService.selectOffer(loanOfferDto);
        log.info("Response status : {}", response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<Void> sendDocuments(UUID statementId) {
        log.info("POST request come on /deal/document/{statementId}/send with {}", statementId);
        ResponseEntity<Void> response = dealRestClientService.sendDocuments(statementId);
        log.info("Response status : {}", response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<Void> signDocuments(UUID statementId) {
        log.info("POST request come on /deal/document/{statementId}/sign with {}", statementId);
        ResponseEntity<Void> response = dealRestClientService.signDocuments(statementId);
        log.info("Response status : {}", response.getStatusCode());
        return response;
    }

    @Override
    public ResponseEntity<List<StatementDto>> getAllStatements() {
        log.info("GET request come on /deal/admin/statement");
        ResponseEntity<List<StatementDto>> response = dealRestClientService.getAllStatements();
        log.info("Response status : {}", response.getStatusCode());
        log.info("Response body : {}", response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<StatementDto> getStatementById(UUID statementId) {
        log.info("GET request come on /deal/admin/statement/{statementId} with {}", statementId);
        ResponseEntity<StatementDto> response = dealRestClientService.getStatementById(statementId);
        log.info("Response status : {}", response.getStatusCode());
        log.info("Response body : {}", response.getBody());
        return response;
    }
}
