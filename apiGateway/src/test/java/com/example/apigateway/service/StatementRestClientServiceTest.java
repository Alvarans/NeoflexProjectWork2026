package com.example.apigateway.service;

import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.apigateway.service.impl.StatementRestClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatementRestClientServiceTest {

    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private ObjectMapper objectMapper;

    private StatementRestClientServiceImpl service;

    @BeforeEach
    void setUp() {

        when(restClientBuilder.baseUrl(anyString()))
                .thenReturn(restClientBuilder);

        when(restClientBuilder.build())
                .thenReturn(restClient);

        service = new StatementRestClientServiceImpl(
                restClientBuilder,
                objectMapper,
                "http://localhost:8080"
        );
    }

    @Test
    void createStatement_shouldReturnLoanOffers() {

        LoanStatementRequestDto requestDto =
                new LoanStatementRequestDto();

        List<LoanOfferDto> offers = List.of(
                new LoanOfferDto(),
                new LoanOfferDto()
        );

        ResponseEntity<List<LoanOfferDto>> expected =
                ResponseEntity.ok(offers);

        when(restClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri("/statement"))
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
                service.createStatement(requestDto);

        assertNotNull(actual);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(2, actual.getBody().size());

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/statement");
        verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
        verify(requestBodySpec).body(requestDto);
    }

    @Test
    void selectOffer_shouldReturnOk() {

        LoanOfferDto loanOfferDto = new LoanOfferDto();

        ResponseEntity<Void> expected =
                ResponseEntity.ok().build();

        when(restClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri("/statement/offer"))
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
                service.selectOffer(loanOfferDto);

        assertNotNull(actual);
        assertEquals(HttpStatus.OK, actual.getStatusCode());

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/statement/offer");
    }

    @Test
    void selectOffer_shouldThrowException_when4xxError() {

        LoanOfferDto loanOfferDto = new LoanOfferDto();

        when(restClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri("/statement/offer"))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.body(loanOfferDto))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
                .thenThrow(
                        new IllegalArgumentException(
                                "Ошибка валидации данных"
                        )
                );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.selectOffer(loanOfferDto)
        );

        assertTrue(
                exception.getMessage()
                        .contains("Ошибка валидации")
        );
    }
}
