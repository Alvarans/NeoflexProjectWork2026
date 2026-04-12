package com.example.deal;

import com.example.deal.controller.DealController;
import com.example.api.common.dto.LoanOfferDto;
import com.example.deal.service.impl.DealServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DealControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DealServiceImpl dealServiceImpl;

    @InjectMocks
    private DealController dealController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(dealController)
                .build();
    }

    @Test
    void testCreateStatement_Success() throws Exception {
        when(dealServiceImpl.makeADealStatement(any())).thenReturn(List.of(new LoanOfferDto()));

        String validJson = """
            {
              "firstName": "Ivan",
              "lastName": "Ivanov",
              "email": "test@test.com",
              "birthdate": "2000-01-01",
              "passportSeries": "1234",
              "passportNumber": "123456",
              "amount": 100000,
              "term": 12
            }
            """;

        mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isOk());

        verify(dealServiceImpl, times(1)).makeADealStatement(any());
    }

    @Test
    void testSelectOffer_Success() throws Exception {
        LoanOfferDto loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(UUID.randomUUID());
        loanOfferDto.setTerm(12);
        loanOfferDto.setTotalAmount(BigDecimal.valueOf(100000));
        loanOfferDto.setRequestedAmount(BigDecimal.valueOf(100000));
        loanOfferDto.setMonthlyPayment(BigDecimal.valueOf(10000));
        loanOfferDto.setRate(BigDecimal.valueOf(15));
        loanOfferDto.setIsInsuranceEnabled(true);
        loanOfferDto.setIsSalaryClient(true);

        doNothing().when(dealServiceImpl).selectOffer(any(LoanOfferDto.class));

        mockMvc.perform(post("/deal/offer/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loanOfferDto)))
                .andExpect(status().isOk());

        verify(dealServiceImpl, times(1)).selectOffer(any(LoanOfferDto.class));
    }

    @Test
    void testCalculateCredit_Success() throws Exception {
        UUID statementId = UUID.randomUUID();
        doNothing().when(dealServiceImpl).calculateCredit(any(), any());

        mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(dealServiceImpl, times(1)).calculateCredit(eq(statementId), any());
    }
}

