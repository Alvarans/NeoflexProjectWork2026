package com.example.statement.service.impl;

import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.statement.service.DealClientService;
import com.example.statement.service.PrescoreService;
import com.example.statement.service.StatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис, реализующий бизнес-логику statement MVP
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatementServiceImpl implements StatementService {
    private final PrescoreService prescoreService;
    private final DealClientService dealClientService;

    @Override
    public List<LoanOfferDto> createOffers(LoanStatementRequestDto loanStatementRequestDto) {
        if (loanStatementRequestDto == null) {
            log.error("loanStatementRequestDto is null");
            throw new IllegalArgumentException("Your request data is null");
        }
        prescoreService.prescore(loanStatementRequestDto);
        log.info("LoanStatementRequestDto goes to offer creation prescored");
        return dealClientService.createOffers(loanStatementRequestDto);
    }

    @Override
    public void selectOffer(LoanOfferDto loanOfferDto) {
        if (loanOfferDto == null) {
            log.error("loanOfferDto is null");
            throw new IllegalArgumentException("Your choosed offer is empty");
        }
        log.info("selected offer {}", loanOfferDto);
        dealClientService.selectOffer(loanOfferDto);
        log.info("offer successfully selected");
    }
}
