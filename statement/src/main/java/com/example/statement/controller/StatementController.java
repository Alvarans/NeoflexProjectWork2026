package com.example.statement.controller;

import com.example.api.common.dto.LoanOfferDto;
import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.statement.api.StatementApi;
import com.example.statement.service.StatementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatementController implements StatementApi {
    private final StatementService statementService;


    @Override
    public ResponseEntity<List<LoanOfferDto>> createStatement(@Valid LoanStatementRequestDto loanStatementRequestDto) {
        return ResponseEntity.ok(statementService.createOffers(loanStatementRequestDto));
    }

    @Override
    public ResponseEntity<Void> selectOffer(@Valid LoanOfferDto loanOfferDto) {
        statementService.selectOffer(loanOfferDto);
        return ResponseEntity.ok().build();
    }
}
