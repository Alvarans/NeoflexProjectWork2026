package com.example.deal.service;

import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.entity.ClientEntity;

import java.util.UUID;
/**
 * Сервис, обслуживающий взаимодействие с клиентом
 */
public interface ClientService {
    /**
     * Формирование записи нового клиента
     * @param loanStatementRequestDto Информация для запроса по кредитным предложениям.
     *                                Содержит первичную информацию по клиенту
     * @return ID созданного клиента для дальнейшего взаимодействия
     */
    UUID createClient(LoanStatementRequestDto loanStatementRequestDto);

    /**
     * Получение записи о клиенте из базы данных
     *
     * @param clientId Идентификационный номер клиента
     * @return Запись о клиенте
     */
    ClientEntity getClientEntityByClientId(UUID clientId);

    /**
     * Дополнение недостающей информации о клиенте после принятия кредитного предложения
     * @param clientId - Идентификационный номер клиента
     * @param finishRegistrationRequestDto - Информация о клиенте после полной регистрации
     */
    void updateClientAfterCreditCalculation(UUID clientId, FinishRegistrationRequestDto finishRegistrationRequestDto);
}
