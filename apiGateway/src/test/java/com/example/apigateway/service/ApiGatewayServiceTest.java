package com.example.apigateway.service;

import com.example.api.common.dto.CreditDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.api.common.dto.ScoringDataDto;
import com.example.apigateway.dto.DocumentCodePassRequest;
import com.example.apigateway.dto.FinishRegistrationRequestDto;
import com.example.apigateway.dto.StatementDto;
import com.example.apigateway.service.impl.ApiGatewayServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiGatewayServiceTest {
    @Mock
    private CalculatorRestClientService calculatorRestClientService;
    @Mock
    private DealRestClientService dealRestClientService;
    @Mock
    private StatementRestClientService statementRestClientService;

    @InjectMocks
    private ApiGatewayServiceImpl apiGatewayService;

    @Test
    void calculateCredit_shouldCallDealService() {
        UUID id = UUID.randomUUID();
        FinishRegistrationRequestDto dto = new FinishRegistrationRequestDto();
        when(dealRestClientService.calculateCredit(id, dto)).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<Void> response = apiGatewayService.calculateCredit(id, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(dealRestClientService, times(1)).calculateCredit(id, dto);
    }

    @Test
    void createDealStatement_shouldCallDealService() {
        LoanStatementRequestDto dto = new LoanStatementRequestDto();
        List<LoanOfferDto> expectedOffers = List.of(new LoanOfferDto());
        when(dealRestClientService.createDealStatement(dto)).thenReturn(ResponseEntity.ok(expectedOffers));

        ResponseEntity<List<LoanOfferDto>> response = apiGatewayService.createDealStatement(dto);

        assertEquals(expectedOffers, response.getBody());
        verify(dealRestClientService).createDealStatement(dto);
    }

    @Test
    void createStatement_shouldCallStatementService() {
        LoanStatementRequestDto dto = new LoanStatementRequestDto();
        when(statementRestClientService.createStatement(dto)).thenReturn(ResponseEntity.ok(List.of()));

        apiGatewayService.createStatement(dto);

        verify(statementRestClientService).createStatement(dto);
    }

    @Test
    void creditCalculation_shouldCallCalculatorService() {
        ScoringDataDto dto = new ScoringDataDto();
        CreditDto expected = new CreditDto();
        when(calculatorRestClientService.creditCalculation(dto)).thenReturn(ResponseEntity.ok(expected));

        ResponseEntity<CreditDto> response = apiGatewayService.creditCalculation(dto);

        assertEquals(expected, response.getBody());
        verify(calculatorRestClientService).creditCalculation(dto);
    }

    @Test
    void getCreditOffers_shouldCallCalculatorService() {
        LoanStatementRequestDto dto = new LoanStatementRequestDto();
        when(calculatorRestClientService.creditOffers(dto)).thenReturn(ResponseEntity.ok(List.of()));

        apiGatewayService.getCreditOffers(dto);

        verify(calculatorRestClientService).creditOffers(dto);
    }

    @Test
    void documentCodePass_shouldCallDealService() {
        UUID id = UUID.randomUUID();
        DocumentCodePassRequest request = new DocumentCodePassRequest();
        when(dealRestClientService.documentCodePass(id, request)).thenReturn(ResponseEntity.ok().build());

        apiGatewayService.documentCodePass(id, request);

        verify(dealRestClientService).documentCodePass(id, request);
    }

    @Test
    void selectDealOffer_shouldCallDealService() {
        LoanOfferDto dto = new LoanOfferDto();
        when(dealRestClientService.selectDealOffer(dto)).thenReturn(ResponseEntity.ok().build());

        apiGatewayService.selectDealOffer(dto);

        verify(dealRestClientService).selectDealOffer(dto);
    }

    @Test
    void selectOffer_shouldCallStatementService() {
        LoanOfferDto dto = new LoanOfferDto();
        when(statementRestClientService.selectOffer(dto)).thenReturn(ResponseEntity.ok().build());

        apiGatewayService.selectOffer(dto);

        verify(statementRestClientService).selectOffer(dto);
    }

    @Test
    void sendDocuments_shouldCallDealService() {
        UUID id = UUID.randomUUID();
        when(dealRestClientService.sendDocuments(id)).thenReturn(ResponseEntity.ok().build());

        apiGatewayService.sendDocuments(id);

        verify(dealRestClientService).sendDocuments(id);
    }

    @Test
    void signDocuments_shouldCallDealService() {
        UUID id = UUID.randomUUID();
        when(dealRestClientService.signDocuments(id)).thenReturn(ResponseEntity.ok().build());

        apiGatewayService.signDocuments(id);

        verify(dealRestClientService).signDocuments(id);
    }

    @Test
    void getAllStatements_shouldCallDealService() {
        List<StatementDto> list = List.of(new StatementDto());
        when(dealRestClientService.getAllStatements()).thenReturn(ResponseEntity.ok(list));

        ResponseEntity<List<StatementDto>> response = apiGatewayService.getAllStatements();

        assertEquals(list, response.getBody());
        verify(dealRestClientService).getAllStatements();
    }

    @Test
    void getStatementById_shouldCallDealService() {
        UUID id = UUID.randomUUID();
        StatementDto dto = new StatementDto();
        when(dealRestClientService.getStatementById(id)).thenReturn(ResponseEntity.ok(dto));

        ResponseEntity<StatementDto> response = apiGatewayService.getStatementById(id);

        assertEquals(dto, response.getBody());
        verify(dealRestClientService).getStatementById(id);
    }
}
