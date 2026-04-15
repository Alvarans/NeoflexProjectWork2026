package com.example.statement;

import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.statement.controller.StatementController;
import com.example.statement.service.StatementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatementControllerTest {
    @Mock
    private StatementService statementService;

    @InjectMocks
    private StatementController statementController;

    @Test
    void shouldReturnOffersWhenCreateStatement() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();

        List<LoanOfferDto> offers = List.of(
                new LoanOfferDto(),
                new LoanOfferDto()
        );

        when(statementService.createOffers(request)).thenReturn(offers);

        ResponseEntity<List<LoanOfferDto>> response =
                statementController.createStatement(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(offers, response.getBody());

        verify(statementService).createOffers(request);
    }

    @Test
    void shouldCallServiceAndReturnOkWhenSelectOffer() {
        LoanOfferDto offer = new LoanOfferDto();

        ResponseEntity<Void> response =
                statementController.selectOffer(offer);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        verify(statementService).selectOffer(offer);
    }
}
