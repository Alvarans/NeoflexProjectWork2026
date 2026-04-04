package com.example.deal.service;

import com.example.deal.dto.*;
import com.example.deal.entity.ClientEntity;
import com.example.deal.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Сервис, обслуживающий взаимодействие с клиентом
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {
    private final ClientRepository clientRepository;

    /**
     * Формирование записи нового клиента
     * @param loanStatementRequestDto Информация для запроса по кредитным предложениям.
     *                                Содержит первичную информацию по клиенту
     * @return ID созданного клиента для дальнейшего взаимодействия
     */
    UUID createClient(LoanStatementRequestDto loanStatementRequestDto) {
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setLastName(loanStatementRequestDto.getLastName());
        clientEntity.setFirstName(loanStatementRequestDto.getFirstName());
        clientEntity.setMiddleName(loanStatementRequestDto.getMiddleName());
        clientEntity.setBirthDate(loanStatementRequestDto.getBirthdate());
        clientEntity.setEmail(loanStatementRequestDto.getEmail());
        clientEntity.setGender(Genders.NON_BINARY);
        clientEntity.setMaritalStatus(MaritalStatus.SINGLE);
        Passport clientPassport = new Passport();
        clientPassport.setSeries(loanStatementRequestDto.getPassportSeries());
        clientPassport.setNumber(loanStatementRequestDto.getPassportNumber());
        clientEntity.setPassport(clientPassport);
        clientRepository.save(clientEntity);
        log.info("New client saved in database {}", clientEntity);
        return  clientEntity.getClientId();
    }

    /**
     * Получение записи о клиенте из базы данных
     * @param clientId Идентификационный номер клиента
     * @return Запись о клиенте
     */
    ClientEntity getClientEntityByClientId(UUID clientId) {
        return clientRepository.getReferenceById(clientId);
    }

    /**
     * Дополнение недостающей информации о клиенте после принятия кредитного предложения
     * @param clientId - Идентификационный номер клиента
     * @param finishRegistrationRequestDto - Информация о клиенте после полной регистрации
     */
    void updateClientAfterCreditCalculation(UUID clientId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        ClientEntity clientEntity = getClientEntityByClientId(clientId);
        clientEntity.setGender(finishRegistrationRequestDto.getGender());
        clientEntity.setMaritalStatus(finishRegistrationRequestDto.getMaritalStatus());
        clientEntity.setDependentAmount(finishRegistrationRequestDto.getDependentAmount());
        clientEntity.getPassport().setIssueDate(finishRegistrationRequestDto.getPassportIssueDate());
        clientEntity.getPassport().setIssueBranch(finishRegistrationRequestDto.getPassportIssueBrach());
        clientEntity.setEmployment(finishRegistrationRequestDto.getEmployment());
        clientEntity.setAccountNumber(finishRegistrationRequestDto.getAccountNumber());
        clientRepository.save(clientEntity);
        log.info("Updated client info after credit calculation in database {}", clientEntity);
    }
}
