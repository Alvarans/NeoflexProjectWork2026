package com.example.calculator.controller;
import com.example.calculator.service.CalculatorService;
import com.example.calculator.service.ScoringService;
import com.example.models.CreditDto;
import com.example.models.LoanOfferDto;
import com.example.models.LoanStatementRequestDto;
import com.example.models.ScoringDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.CalculatorApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/calculator")
@Validated
@Slf4j
//@Tag(name = "Credit controller", description = "Контроллер для вычисления кредитных предложений")
public class CalculatorController implements CalculatorApi {
    //Service for api logic
    private final CalculatorService calculatorService;
    //Service for prescoring and scoring data
    private final ScoringService scoringService;

    /**
     * End-point for creation credit offers. Information about client must pass prescore
     *
     * @param loanStatementRequestDto - information about client
     * @return list of credit offers. If information about client can't pass prescore - return empty list
     */
    @Override
    //@PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> creditOffers(@Validated @RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        scoringService.prescore(loanStatementRequestDto);
        return ResponseEntity.ok(calculatorService.generateOffers(loanStatementRequestDto));
    }

    /**
     * End-point for creation credit for client. Request information must pass validation and score
     *
     * @param scoringDataDto - information about client and requested credit
     * @return filled credit offer with monthly payment schedule. If request information can't pass score - return empty credit offer
     */
    @Override
    //@PostMapping("/calc")
    public ResponseEntity<CreditDto> creditCalculation(@Validated @RequestBody ScoringDataDto scoringDataDto) {
        BigDecimal scoreRate;
        try {
            scoreRate = scoringService.score(scoringDataDto);
        } catch (IllegalArgumentException iae) {
            log.error("Scoring couldn't passed because of " + iae.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CreditDto());
        }
        return ResponseEntity.ok(calculatorService.createCredit(scoringDataDto, scoreRate));
    }


}
