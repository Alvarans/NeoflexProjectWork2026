package com.example.deal.service;

import com.example.deal.dto.ApplicationStatus;
import com.example.deal.entity.StatementEntity;
import com.example.deal.repository.StatementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatementService {
    private final StatementRepository statementRepository;
    private final ClientService clientService;

    public UUID createStatement(UUID clientId) {
        StatementEntity statementEntity = new StatementEntity();
        statementEntity.setClient(clientService.getClientEntityByClientId(clientId));
        statementEntity.setApplicationStatus(ApplicationStatus.PREAPPROVAL);
        statementEntity.setCreationDate(LocalDateTime.now());
        statementRepository.save(statementEntity);
        return statementEntity.getStatementId();
    }
}
