package com.example.deal.Service;

import com.example.api.common.dto.CreditDto;
import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.api.common.dto.ScoringDataDto;
import com.example.deal.service.impl.CalculatorRestClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
public class CalculatorRestClientServiceTest {
    private CalculatorRestClientServiceImpl service;
    private MockRestServiceServer server;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();

        service = new CalculatorRestClientServiceImpl(builder, objectMapper, "http://localhost:8010");
    }

    @Test
    void getOffers_shouldReturnFourRankedOffers() {
        LoanOfferDto offer1 = new LoanOfferDto();
        offer1.setIsInsuranceEnabled(false);
        offer1.setIsSalaryClient(false);

        LoanOfferDto offer2 = new LoanOfferDto();
        offer2.setIsInsuranceEnabled(true);
        offer2.setIsSalaryClient(false);

        LoanOfferDto offer3 = new LoanOfferDto();
        offer3.setIsInsuranceEnabled(false);
        offer3.setIsSalaryClient(true);

        LoanOfferDto offer4 = new LoanOfferDto();
        offer4.setIsInsuranceEnabled(true);
        offer4.setIsSalaryClient(true);

        List<LoanOfferDto> expectedOffers = List.of(
                createOffer(false, false),
                createOffer(true, false),
                createOffer(false, true),
                createOffer(true, true)
        );

        server.expect(requestTo("http://localhost:8010/calculator/offers"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(
                        objectMapper.writeValueAsString(expectedOffers),
                        MediaType.APPLICATION_JSON
                ));

        List<LoanOfferDto> actualOffers =
                service.getOffers(new LoanStatementRequestDto());

        assertNotNull(actualOffers);
        assertEquals(4, actualOffers.size());

        // Проверка ранжирования
        assertFalse(actualOffers.get(0).getIsInsuranceEnabled());
        assertFalse(actualOffers.get(0).getIsSalaryClient());

        assertTrue(actualOffers.get(1).getIsInsuranceEnabled());
        assertFalse(actualOffers.get(1).getIsSalaryClient());

        assertFalse(actualOffers.get(2).getIsInsuranceEnabled());
        assertTrue(actualOffers.get(2).getIsSalaryClient());

        assertTrue(actualOffers.get(3).getIsInsuranceEnabled());
        assertTrue(actualOffers.get(3).getIsSalaryClient());

        server.verify();
    }

    @Test
    void calculateCredit_shouldReturnCreditDto() {
        CreditDto expectedCredit = new CreditDto();
        expectedCredit.setAmount(BigDecimal.valueOf(500000));

        server.expect(requestTo("http://localhost:8010/calculator/calc"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Content-Type", "application/json"))
                .andRespond(withSuccess(
                        objectMapper.writeValueAsString(expectedCredit),
                        MediaType.APPLICATION_JSON
                ));

        CreditDto actualCredit =
                service.calculateCredit(new ScoringDataDto());

        assertNotNull(actualCredit);
        assertEquals(BigDecimal.valueOf(500000), actualCredit.getAmount());

        server.verify();
    }

    @Test
    void calculateCredit_shouldThrowIllegalArgumentException_when4xxError() {
        CreditDto emptyCreditDto = new CreditDto();

        server.expect(requestTo("http://localhost:8010/calculator/calc"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(emptyCreditDto)));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateCredit(new ScoringDataDto())
        );

        assertEquals(
                "Check your data. Validation goes wrong",
                exception.getMessage()
        );

        server.verify();
    }

    private LoanOfferDto createOffer(boolean insurance, boolean salary) {
        LoanOfferDto dto = new LoanOfferDto();

        dto.setStatementId(UUID.randomUUID());
        dto.setRequestedAmount(BigDecimal.valueOf(100000));
        dto.setTotalAmount(BigDecimal.valueOf(120000));
        dto.setTerm(12);
        dto.setMonthlyPayment(BigDecimal.valueOf(10000));
        dto.setRate(BigDecimal.valueOf(15));

        dto.setIsInsuranceEnabled(insurance);
        dto.setIsSalaryClient(salary);

        return dto;
    }
}