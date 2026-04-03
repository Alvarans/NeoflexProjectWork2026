package com.example.deal.service;

import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.entity.ClientEntity;
import com.example.deal.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public void createClient(LoanStatementRequestDto loanStatementRequestDto) {
        ClientEntity clientEntity = new ClientEntity();

    }

    private ClientEntity createClientEntityByLoanStatementRequest(LoanStatementRequestDto loanStatementRequestDto) {
        ClientEntity clientEntity = new ClientEntity();
        return clientEntity;
    }
}
