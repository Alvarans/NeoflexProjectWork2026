package com.example.deal.Service;

import com.example.deal.dto.ApplicationStatus;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.entity.ClientEntity;
import com.example.deal.entity.StatementEntity;
import com.example.deal.repository.StatementRepository;
import com.example.deal.service.ClientService;
import com.example.deal.service.StatementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatementServiceTest {

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private StatementService statementService;

    @Test
    void testCreateStatement_Success() {
        UUID clientId = UUID.randomUUID();
        ClientEntity clientEntity = new ClientEntity();
        StatementEntity savedStatement = new StatementEntity();
        UUID statementId = UUID.randomUUID();
        savedStatement.setStatementId(statementId);

        when(clientService.getClientEntityByClientId(clientId)).thenReturn(clientEntity);
        when(statementRepository.save(any(StatementEntity.class))).thenAnswer(invocation -> {
            StatementEntity entity = invocation.getArgument(0);
            entity.setStatementId(statementId); // эмулируем авто-генерацию ID
            return entity;
        });

        UUID resultId = statementService.createStatement(clientId);

        assertEquals(statementId, resultId);
        verify(statementRepository, times(1)).save(any(StatementEntity.class));
        verify(clientService, times(1)).getClientEntityByClientId(clientId);
    }

    @Test
    void testUpdateStatementWithChoosedOffer_Success() {
        UUID statementId = UUID.randomUUID();
        StatementEntity statementEntity = new StatementEntity();
        LoanOfferDto loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(statementId);

        when(statementRepository.findByStatementId(statementId)).thenReturn(statementEntity);

        statementService.updateStatementWithChoosedOffer(loanOfferDto);

        assertEquals(loanOfferDto, statementEntity.getAppliedOffer());
        assertEquals(ApplicationStatus.APPROVED, statementEntity.getApplicationStatus());
        verify(statementRepository, times(1)).save(statementEntity);
    }

    @Test
    void testUpdateStatementWithChoosedOffer_StatementNotFound() {
        UUID statementId = UUID.randomUUID();
        LoanOfferDto loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(statementId);

        when(statementRepository.findByStatementId(statementId)).thenReturn(null);

        Exception exception = assertThrows(NullPointerException.class, () -> statementService.updateStatementWithChoosedOffer(loanOfferDto));

        assertTrue(exception.getMessage() == null || exception.getMessage().contains("null"));
        verify(statementRepository, times(1)).findByStatementId(statementId);
        verify(statementRepository, never()).save(any(StatementEntity.class));
    }

    @Test
    void getStatement_shouldReturnStatementEntity() {
        UUID statementId = UUID.randomUUID();
        StatementEntity expected = new StatementEntity();

        when(statementRepository.findByStatementId(statementId))
                .thenReturn(expected);

        StatementEntity actual =
                statementService.getStatement(statementId);

        assertEquals(expected, actual);
        verify(statementRepository).findByStatementId(statementId);
    }
}
