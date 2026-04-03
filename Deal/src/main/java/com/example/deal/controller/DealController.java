package com.example.deal.controller;

import com.example.deal.api.DealApi;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.service.DealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class DealController implements DealApi {
    private final DealService dealService;
    @Override
    public ResponseEntity<Void> calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<LoanOfferDto>> createStatement(@Validated @RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        return ResponseEntity.ok(dealService.makeADealStatement(loanStatementRequestDto));
    }

    @Override
    public ResponseEntity<Void> selectOffer(LoanOfferDto loanOfferDto) {
        return ResponseEntity.ok().build();
    }
}
