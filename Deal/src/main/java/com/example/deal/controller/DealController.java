package com.example.deal.controller;

import com.example.deal.api.DealApi;
import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.deal.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor

@Validated
@Slf4j
public class DealController extends DealApi {
    private final ClientService clientService;
//    @Override
//    public ResponseEntity<List<LoanOfferDto>> calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
//        return null;
//    }

    @Override
    public ResponseEntity<List<LoanOfferDto>> createStatement(@Validated @RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        return null;
    }

//    @Override
//    public ResponseEntity<Void> selectOffer(LoanOfferDto loanOfferDto) {
//        return null;
//    }
}
