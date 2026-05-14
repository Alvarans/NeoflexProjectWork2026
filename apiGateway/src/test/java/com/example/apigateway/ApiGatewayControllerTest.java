package com.example.apigateway;

import com.example.api.common.dto.CreditDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.api.common.dto.ScoringDataDto;
import com.example.apigateway.controller.ApiGatewayController;
import com.example.apigateway.dto.DocumentCodePassRequest;
import com.example.apigateway.dto.FinishRegistrationRequestDto;
import com.example.apigateway.dto.StatementDto;
import com.example.apigateway.service.ApiGatewayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiGatewayControllerTest {

    @Mock
    private ApiGatewayService apiGatewayService;

    @InjectMocks
    private ApiGatewayController controller;

    private UUID statementId;

    @BeforeEach
    void setUp() {
        statementId = UUID.randomUUID();
    }

    @Test
    void calculateCredit_shouldReturnOk() {

        FinishRegistrationRequestDto request =
                new FinishRegistrationRequestDto();

        ResponseEntity<Void> expected =
                ResponseEntity.ok().build();

        when(apiGatewayService.calculateCredit(statementId, request))
                .thenReturn(expected);

        ResponseEntity<Void> actual =
                controller.calculateCredit(statementId, request);

        assertEquals(expected, actual);

        verify(apiGatewayService)
                .calculateCredit(statementId, request);
    }

    @Test
    void createDealStatement_shouldReturnOffers() {

        LoanStatementRequestDto request =
                new LoanStatementRequestDto();

        List<LoanOfferDto> offers = List.of(
                new LoanOfferDto()
        );

        ResponseEntity<List<LoanOfferDto>> expected =
                ResponseEntity.ok(offers);

        when(apiGatewayService.createDealStatement(request))
                .thenReturn(expected);

        ResponseEntity<List<LoanOfferDto>> actual =
                controller.createDealStatement(request);

        assertEquals(expected, actual);

        verify(apiGatewayService)
                .createDealStatement(request);
    }

    @Test
    void createStatement_shouldReturnOffers() {

        LoanStatementRequestDto request =
                new LoanStatementRequestDto();

        List<LoanOfferDto> offers = List.of(
                new LoanOfferDto()
        );

        ResponseEntity<List<LoanOfferDto>> expected =
                ResponseEntity.ok(offers);

        when(apiGatewayService.createStatement(request))
                .thenReturn(expected);

        ResponseEntity<List<LoanOfferDto>> actual =
                controller.createStatement(request);

        assertEquals(expected, actual);

        verify(apiGatewayService)
                .createStatement(request);
    }

    @Test
    void creditCalculation_shouldReturnCreditDto() {

        ScoringDataDto request =
                new ScoringDataDto();

        CreditDto creditDto = new CreditDto();

        ResponseEntity<CreditDto> expected =
                ResponseEntity.ok(creditDto);

        when(apiGatewayService.creditCalculation(request))
                .thenReturn(expected);

        ResponseEntity<CreditDto> actual =
                controller.creditCalculation(request);

        assertEquals(expected, actual);

        verify(apiGatewayService)
                .creditCalculation(request);
    }

    @Test
    void creditOffers_shouldReturnOffers() {

        LoanStatementRequestDto request =
                new LoanStatementRequestDto();

        List<LoanOfferDto> offers = List.of(
                new LoanOfferDto()
        );

        ResponseEntity<List<LoanOfferDto>> expected =
                ResponseEntity.ok(offers);

        when(apiGatewayService.getCreditOffers(request))
                .thenReturn(expected);

        ResponseEntity<List<LoanOfferDto>> actual =
                controller.creditOffers(request);

        assertEquals(expected, actual);

        verify(apiGatewayService)
                .getCreditOffers(request);
    }

    @Test
    void documentCodePass_shouldReturnOk() {

        DocumentCodePassRequest request =
                new DocumentCodePassRequest();

        ResponseEntity<Void> expected =
                ResponseEntity.ok().build();

        when(apiGatewayService.documentCodePass(statementId, request))
                .thenReturn(expected);

        ResponseEntity<Void> actual =
                controller.documentCodePass(statementId, request);

        assertEquals(expected, actual);

        verify(apiGatewayService)
                .documentCodePass(statementId, request);
    }

    @Test
    void getAllStatements_shouldReturnStatements() {

        List<StatementDto> statements = List.of(
                new StatementDto()
        );

        ResponseEntity<List<StatementDto>> expected =
                ResponseEntity.ok(statements);

        when(apiGatewayService.getAllStatements())
                .thenReturn(expected);

        ResponseEntity<List<StatementDto>> actual =
                controller.getAllStatements();

        assertEquals(expected, actual);

        verify(apiGatewayService)
                .getAllStatements();
    }

    @Test
    void getStatementById_shouldReturnStatement() {

        StatementDto statementDto = new StatementDto();

        ResponseEntity<StatementDto> expected =
                ResponseEntity.ok(statementDto);

        when(apiGatewayService.getStatementById(statementId))
                .thenReturn(expected);

        ResponseEntity<StatementDto> actual =
                controller.getStatementById(statementId);

        assertEquals(expected, actual);

        verify(apiGatewayService)
                .getStatementById(statementId);
    }

    @Test
    void selectDealOffer_shouldReturnOk() {

        LoanOfferDto request = new LoanOfferDto();

        ResponseEntity<Void> expected =
                ResponseEntity.ok().build();

        when(apiGatewayService.selectDealOffer(request))
                .thenReturn(expected);

        ResponseEntity<Void> actual =
                controller.selectDealOffer(request);

        assertEquals(expected, actual);

        verify(apiGatewayService)
                .selectDealOffer(request);
    }

    @Test
    void selectOffer_shouldReturnOk() {

        LoanOfferDto request = new LoanOfferDto();

        ResponseEntity<Void> expected =
                ResponseEntity.ok().build();

        when(apiGatewayService.selectOffer(request))
                .thenReturn(expected);

        ResponseEntity<Void> actual =
                controller.selectOffer(request);

        assertEquals(expected, actual);

        verify(apiGatewayService)
                .selectOffer(request);
    }

    @Test
    void sendDocuments_shouldReturnOk() {

        ResponseEntity<Void> expected =
                ResponseEntity.ok().build();

        when(apiGatewayService.sendDocuments(statementId))
                .thenReturn(expected);

        ResponseEntity<Void> actual =
                controller.sendDocuments(statementId);

        assertEquals(expected, actual);

        verify(apiGatewayService)
                .sendDocuments(statementId);
    }

    @Test
    void signDocuments_shouldReturnOk() {

        ResponseEntity<Void> expected =
                ResponseEntity.ok().build();

        when(apiGatewayService.signDocuments(statementId))
                .thenReturn(expected);

        ResponseEntity<Void> actual =
                controller.signDocuments(statementId);

        assertEquals(expected, actual);

        verify(apiGatewayService)
                .signDocuments(statementId);
    }
}

