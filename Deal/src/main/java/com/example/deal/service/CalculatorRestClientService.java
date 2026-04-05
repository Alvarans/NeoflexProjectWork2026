package com.example.deal.service;

import com.example.deal.dto.CreditDto;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.dto.ScoringDataDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Rest client сервис для отправления запросов в Calculator
 */
@Service
public class CalculatorRestClientService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public CalculatorRestClientService(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder
                .baseUrl("http://localhost:8010")
                .build();
    }

    /**
     * Получение списка из 4 предложений от Calculator
     * @param loanStatementRequestDto - Информация для запроса по кредитным предложениям
     * @return - список из 4 предложений от худшего к лучшему
     */
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto loanStatementRequestDto) {
        return restClient.post()
                .uri("/calculator/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loanStatementRequestDto)
                .retrieve()
                .body(new ParameterizedTypeReference<List<LoanOfferDto>>() {});
    }

    /**
     * Подсчёт кредита для клиента, проводимый в Calculator
     * @param scoringDataDto - собранная информация о клиенте, кредите и сотруднике, обслуживающем его
     * @return Объект кредита, содержащий в себе основную информацию о нём + график платежей
     */
    public CreditDto calculateCredit(ScoringDataDto scoringDataDto) {
        return restClient.post()
                .uri("/calculator/calc")
                .contentType(MediaType.APPLICATION_JSON)
                .body(scoringDataDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    // Извлекаем тело (пустой CreditDto согласно вашей спеке)
                    CreditDto errorDto = objectMapper.readValue(response.getBody(), CreditDto.class);
                    throw new IllegalArgumentException("Check your data. Validation goes wrong");
                })
                .body(CreditDto.class);
    }
}
