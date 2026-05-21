package com.example.apigateway.service;

import com.example.api.common.dto.CreditDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.api.common.dto.ScoringDataDto;
import com.example.apigateway.service.impl.CalculatorRestClientServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatorRestClientServiceTest {
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

    private CalculatorRestClientServiceImpl service;

    @BeforeEach
    void setUp() {

        when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);

        service = new CalculatorRestClientServiceImpl(
                restClientBuilder,
                objectMapper,
                "http://localhost:8010"
        );
    }

    @Test
    void creditOffers_shouldReturnListOfOffers() {

        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();

        List<LoanOfferDto> offers = List.of(
                new LoanOfferDto(),
                new LoanOfferDto()
        );

        ResponseEntity<List<LoanOfferDto>> expectedResponse =
                ResponseEntity.ok(offers);

        when(restClient.post()).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri("/calculator/offers"))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.body(requestDto))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<List<LoanOfferDto>> actual =
                service.creditOffers(requestDto);

        assertNotNull(actual);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(2, actual.getBody().size());

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/calculator/offers");
        verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
        verify(requestBodySpec).body(requestDto);
    }

    @Test
    void creditCalculation_shouldReturnCreditDto() {

        ScoringDataDto scoringDataDto = new ScoringDataDto();

        CreditDto creditDto = new CreditDto();

        ResponseEntity<CreditDto> expectedResponse =
                ResponseEntity.ok(creditDto);

        when(restClient.post()).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri("/calculator/calc"))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.body(scoringDataDto))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);

        when(responseSpec.toEntity(CreditDto.class))
                .thenReturn(expectedResponse);

        ResponseEntity<CreditDto> actual =
                service.creditCalculation(scoringDataDto);

        assertNotNull(actual);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(creditDto, actual.getBody());

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/calculator/calc");
        verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
        verify(requestBodySpec).body(scoringDataDto);
    }

    @Test
    void creditCalculation_shouldThrowException_when4xxError() {

        ScoringDataDto scoringDataDto = new ScoringDataDto();

        when(restClient.post()).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri("/calculator/calc"))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.body(scoringDataDto))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
                .thenThrow(new IllegalArgumentException(
                        "Check your data. Validation goes wrong"
                ));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.creditCalculation(scoringDataDto)
        );

        assertEquals(
                "Check your data. Validation goes wrong",
                exception.getMessage()
        );
    }
}

