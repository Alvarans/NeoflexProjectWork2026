package com.example.statement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.statement.service.impl.StatementServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {
    @Mock
    private PrescoreService prescoreService;

    @Mock
    private DealClientService dealClientService;

    @InjectMocks
    private StatementServiceImpl statementService;

    @Test
    @DisplayName("createOffers: успех — прескоринг пройден, офферы получены")
    void createOffers_Success() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        List<LoanOfferDto> expectedOffers = List.of(new LoanOfferDto(), new LoanOfferDto());

        when(dealClientService.createOffers(requestDto)).thenReturn(expectedOffers);

        List<LoanOfferDto> result = statementService.createOffers(requestDto);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(prescoreService, times(1)).prescore(requestDto);
        verify(dealClientService, times(1)).createOffers(requestDto);
    }

    @Test
    @DisplayName("createOffers: ошибка — запрос равен null")
    void createOffers_NullRequest_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            statementService.createOffers(null);
        });

        assertEquals("Your request data is null", exception.getMessage());
        verifyNoInteractions(prescoreService);
        verifyNoInteractions(dealClientService);
    }

    @Test
    @DisplayName("selectOffer: успех — оффер передан в dealClientService")
    void selectOffer_Success() {
        LoanOfferDto offerDto = new LoanOfferDto();
        statementService.selectOffer(offerDto);
        verify(dealClientService, times(1)).selectOffer(offerDto);
    }

    @Test
    @DisplayName("selectOffer: ошибка — оффер равен null")
    void selectOffer_NullOffer_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            statementService.selectOffer(null);
        });

        assertEquals("Your choosed offer is empty", exception.getMessage());
        verifyNoInteractions(dealClientService);
    }
}
