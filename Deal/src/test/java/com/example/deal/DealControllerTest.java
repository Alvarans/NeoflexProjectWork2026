package com.example.deal;

import com.example.deal.controller.DealController;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.service.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
    private DealService dealService;

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
        when(dealService.makeADealStatement(any())).thenReturn(List.of(new LoanOfferDto()));

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

        verify(dealService, times(1)).makeADealStatement(any());
    }

    @Test
    void testSelectOffer_Success() throws Exception {
        doNothing().when(dealService).selectOffer(any());

        mockMvc.perform(post("/deal/offer/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(dealService, times(1)).selectOffer(any());
    }

    @Test
    void testCalculateCredit_Success() throws Exception {
        UUID statementId = UUID.randomUUID();
        doNothing().when(dealService).calculateCredit(any(), any());

        mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(dealService, times(1)).calculateCredit(eq(statementId), any());
    }
}

