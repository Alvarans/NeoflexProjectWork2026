package com.example.deal.service;

import com.example.deal.dto.Genders;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.dto.MaritalStatus;
import com.example.deal.dto.Passport;
import com.example.deal.entity.ClientEntity;
import com.example.deal.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public UUID createClient(LoanStatementRequestDto loanStatementRequestDto) {
        ClientEntity clientEntity = createClientEntityByLoanStatementRequest(loanStatementRequestDto);
        clientRepository.save(clientEntity);
        return  clientEntity.getClientId();
    }

    //Package-private access
    ClientEntity getClientEntityByClientId(UUID clientId) {
        return clientRepository.getReferenceById(clientId);
    }

    private ClientEntity createClientEntityByLoanStatementRequest(LoanStatementRequestDto loanStatementRequestDto) {
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setLastName(loanStatementRequestDto.getLastName());
        clientEntity.setFirstName(loanStatementRequestDto.getFirstName());
        clientEntity.setMiddleName(loanStatementRequestDto.getMiddleName());
        clientEntity.setBirthDate(loanStatementRequestDto.getBirthdate());
        clientEntity.setEmail(loanStatementRequestDto.getEmail());
        //gender
        clientEntity.setGender(Genders.NON_BINARY);
        //marital status
        clientEntity.setMaritalStatus(MaritalStatus.SINGLE);
        //passport
        Passport clientPassport = new Passport();
        clientPassport.setSeries(loanStatementRequestDto.getPassportSeries());
        clientPassport.setNumber(loanStatementRequestDto.getPassportNumber());
        clientEntity.setPassport(clientPassport);
        //employment
        return clientEntity;
    }
}
