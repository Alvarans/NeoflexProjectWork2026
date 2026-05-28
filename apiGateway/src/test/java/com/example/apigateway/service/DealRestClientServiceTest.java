package com.example.apigateway.service;

import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.apigateway.dto.DocumentCodePassRequest;
import com.example.apigateway.dto.FinishRegistrationRequestDto;
import com.example.apigateway.dto.StatementDto;
import com.example.apigateway.service.impl.DealRestClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealRestClientServiceTest {
    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private ObjectMapper objectMapper;

    private DealRestClientServiceImpl service;

    @BeforeEach
    void setUp() {

        when(restClientBuilder.baseUrl(anyString()))
                .thenReturn(restClientBuilder);

        when(restClientBuilder.build())
                .thenReturn(restClient);

        service = new DealRestClientServiceImpl(
                restClientBuilder,
                objectMapper,
                "http://localhost:8011"
        );
    }

    @Test
    void createDealStatement_shouldReturnOffers() {

        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();

        List<LoanOfferDto> offers = List.of(
                new LoanOfferDto(),
                new LoanOfferDto()
        );

        ResponseEntity<List<LoanOfferDto>> expected =
                ResponseEntity.ok(offers);

        when(restClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri("deal/statement"))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.body(requestDto))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(expected);

        ResponseEntity<List<LoanOfferDto>> actual =
                service.createDealStatement(requestDto);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(2, actual.getBody().size());

        verify(restClient).post();
    }

    @Test
    void selectDealOffer_shouldReturnOk() {

        LoanOfferDto loanOfferDto = new LoanOfferDto();

        ResponseEntity<Void> expected =
                ResponseEntity.ok().build();

        when(restClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri("deal/offer/select"))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.body(loanOfferDto))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);

        when(responseSpec.toEntity(Void.class))
                .thenReturn(expected);

        ResponseEntity<Void> actual =
                service.selectDealOffer(loanOfferDto);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
    }

    @Test
    void selectDealOffer_shouldThrowException_when4xxError() {

        LoanOfferDto loanOfferDto = new LoanOfferDto();

        when(restClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri("deal/offer/select"))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.body(loanOfferDto))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
                .thenThrow(new IllegalArgumentException(
                        "Ошибка валидации данных"
                ));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.selectDealOffer(loanOfferDto)
        );

        assertTrue(ex.getMessage().contains("Ошибка валидации"));
    }

    @Test
    void documentCodePass_shouldReturnOk() {

        UUID statementId = UUID.randomUUID();

        DocumentCodePassRequest request = new DocumentCodePassRequest();

        ResponseEntity<Void> expected = ResponseEntity.ok().build();

        when(restClient.post()).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(
                "deal/document/{statementId}/code",
                statementId
        )).thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.body(request))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);

        when(responseSpec.toBodilessEntity())
                .thenReturn(expected);

        ResponseEntity<Void> actual =
                service.documentCodePass(statementId, request);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
    }

    @Test
    void sendDocuments_shouldReturnOk() {

        UUID statementId = UUID.randomUUID();

        ResponseEntity<Void> expected =
                ResponseEntity.ok().build();

        when(restClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(
                "deal/document/{statementId}/send",
                statementId
        )).thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);

        when(responseSpec.toBodilessEntity())
                .thenReturn(expected);

        ResponseEntity<Void> actual =
                service.sendDocuments(statementId);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
    }

    @Test
    void signDocuments_shouldReturnOk() {

        UUID statementId = UUID.randomUUID();

        ResponseEntity<Void> expected =
                ResponseEntity.ok().build();

        when(restClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(
                "deal/document/{statementId}/sign",
                statementId
        )).thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);

        when(responseSpec.toBodilessEntity())
                .thenReturn(expected);

        ResponseEntity<Void> actual =
                service.signDocuments(statementId);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
    }

    @Test
    void calculateCredit_shouldReturnOk() {

        UUID statementId = UUID.randomUUID();

        FinishRegistrationRequestDto request =
                new FinishRegistrationRequestDto();

        ResponseEntity<Void> expected =
                ResponseEntity.ok().build();

        when(restClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(
                "/deal/calculate/{statementId}",
                statementId
        )).thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.body(request))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);

        when(responseSpec.toBodilessEntity())
                .thenReturn(expected);

        ResponseEntity<Void> actual =
                service.calculateCredit(statementId, request);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
    }

    @Test
    void getAllStatements_shouldReturnStatements() {

        List<StatementDto> statements = List.of(
                new StatementDto(),
                new StatementDto()
        );

        ResponseEntity<List<StatementDto>> expected =
                ResponseEntity.ok(statements);

        when(restClient.get())
                .thenReturn(requestHeadersUriSpec);

        when(requestHeadersUriSpec.uri("/deal/admin/statement"))
                .thenReturn(requestHeadersSpec);

        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(expected);

        ResponseEntity<List<StatementDto>> actual =
                service.getAllStatements();

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(2, actual.getBody().size());
    }

    @Test
    void getStatementById_shouldReturnStatement() {
        UUID statementId = UUID.randomUUID();

        StatementDto statementDto = new StatementDto();

        ResponseEntity<StatementDto> expected =
                ResponseEntity.ok(statementDto);

        when(restClient.get())
                .thenReturn(requestHeadersUriSpec);

        when(requestHeadersUriSpec.uri(
                "/deal/admin/statement/{statementId}",
                statementId
        )).thenReturn(requestHeadersSpec);

        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);

        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(expected);

        ResponseEntity<StatementDto> actual =
                service.getStatementById(statementId);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(statementDto, actual.getBody());
    }
}