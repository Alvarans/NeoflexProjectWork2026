package com.example.deal.Service;

import com.example.api.common.dto.*;
import com.example.deal.dto.Passport;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.entity.ClientEntity;
import com.example.deal.repository.ClientRepository;
import com.example.deal.service.impl.ClientServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientServiceImpl;

    @Test
    void createClient_shouldSaveClientAndReturnId() {
        LoanStatementRequestDto dto = new LoanStatementRequestDto();
        dto.setFirstName("Ivan");
        dto.setLastName("Ivanov");
        dto.setMiddleName("Ivanovich");
        dto.setBirthdate(LocalDate.of(1990, 1, 1));
        dto.setEmail("ivan@test.com");
        dto.setPassportSeries("1234");
        dto.setPassportNumber("567890");

        UUID clientId = UUID.randomUUID();

        doAnswer(invocation -> {
            ClientEntity entity = invocation.getArgument(0);
            entity.setClientId(clientId);
            return entity;
        }).when(clientRepository).save(any(ClientEntity.class));

        UUID result = clientServiceImpl.createClient(dto);

        assertEquals(clientId, result);

        ArgumentCaptor<ClientEntity> captor =
                ArgumentCaptor.forClass(ClientEntity.class);

        verify(clientRepository).save(captor.capture());

        ClientEntity savedClient = captor.getValue();

        assertEquals("Ivan", savedClient.getFirstName());
        assertEquals("Ivanov", savedClient.getLastName());
        assertEquals("Ivanovich", savedClient.getMiddleName());
        assertEquals("ivan@test.com", savedClient.getEmail());
        assertEquals("1234", savedClient.getPassport().getSeries());
        assertEquals("567890", savedClient.getPassport().getNumber());
        assertEquals(Genders.NON_BINARY, savedClient.getGender());
        assertEquals(MaritalStatus.SINGLE, savedClient.getMaritalStatus());
    }

    @Test
    void getClientEntityByClientId_shouldReturnClientEntity() {
        UUID clientId = UUID.randomUUID();
        ClientEntity expected = new ClientEntity();

        when(clientRepository.findByClientId(clientId))
                .thenReturn(Optional.of(expected));

        ClientEntity actual =
                clientServiceImpl.getClientEntityByClientId(clientId);

        assertEquals(expected, actual);
        verify(clientRepository).findByClientId(clientId);
    }

    @Test
    void updateClientAfterCreditCalculation_shouldUpdateAndSaveClient() {
        UUID clientId = UUID.randomUUID();

        ClientEntity existingClient = new ClientEntity();
        existingClient.setPassport(new Passport());

        FinishRegistrationRequestDto dto =
                new FinishRegistrationRequestDto();
        dto.setGender(Genders.MALE);
        dto.setMaritalStatus(MaritalStatus.MARRIED);
        dto.setDependentAmount(2);
        dto.setPassportIssueDate(LocalDate.of(2020, 1, 1));
        dto.setPassportIssueBrach("MVD");
        dto.setEmployment(new EmploymentDto());
        dto.setAccountNumber("123456789");

        when(clientRepository.findByClientId(clientId))
                .thenReturn(Optional.of(existingClient));

        clientServiceImpl.updateClientAfterCreditCalculation(clientId, dto);

        verify(clientRepository).save(existingClient);

        assertEquals(Genders.MALE, existingClient.getGender());
        assertEquals(MaritalStatus.MARRIED,
                existingClient.getMaritalStatus());
        assertEquals(2, existingClient.getDependentAmount());
        assertEquals(LocalDate.of(2020, 1, 1),
                existingClient.getPassport().getIssueDate());
        assertEquals("MVD",
                existingClient.getPassport().getIssueBranch());
        assertEquals("123456789",
                existingClient.getAccountNumber());
    }
}
