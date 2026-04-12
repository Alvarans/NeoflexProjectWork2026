package com.example.calculator.service;

import com.example.api.common.dto.ScoringDataDto;

import java.math.BigDecimal;


public interface ScoringService {

    /**
     * Method for scoring request dto according to certain rules
     *
     * @param scoringDataDto - requested dto. Contains scoring information
     * @return adding rate for credit if all right. Otherwise, it throws Illegal Argument Exception
     */
    BigDecimal score(ScoringDataDto scoringDataDto);

    /**
     * Method for calculating amount of money, which bank ready to give a client
     *
     * @param amount             - required amount
     * @param isInsuranceEnabled - include insurance in amount or not
     * @param isSalaryClient     - if client has status "salary client", he doesn't need to pay for insurance
     * @return total amount
     */
    BigDecimal calculateTotalAmount(BigDecimal amount, boolean isInsuranceEnabled, boolean isSalaryClient);

    /**
     * Method for calculating bank rate, using based rate
     *
     * @param isInsuranceEnabled - include insurance in credit or not
     * @param isSalaryClient     - client has status "salary client" or not
     * @return current bank rate
     */
    BigDecimal calculateRate(boolean isInsuranceEnabled, boolean isSalaryClient);

    /**
     * Method for calculating credit monthly payment
     *
     * @param amount - total amount of credit money
     * @param rate   - bank rate
     * @param term   - term of credit
     * @return monthly payment
     */
    BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, Integer term);

    /**
     * Method for calculating total payment amount
     *
     * @param monthlyPayment - monthly payment for credit
     * @param term           - term of credit
     * @return total payment amount
     */
    BigDecimal calculatePSK(BigDecimal monthlyPayment, Integer term);
}
