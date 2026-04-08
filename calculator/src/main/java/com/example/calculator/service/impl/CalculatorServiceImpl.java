package com.example.calculator.service.impl;

import com.example.api.common.dto.*;
import com.example.calculator.service.CalculatorService;
import com.example.calculator.service.ScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculatorServiceImpl implements CalculatorService {
    private final ScoringService scoringService;

    /**
     * Method generate all kind of credit offers
     *
     * @param requestDto - request with information about client
     * @return List of offers
     */
    public List<LoanOfferDto> generateOffers(LoanStatementRequestDto requestDto) {
        List<LoanOfferDto> offers = new ArrayList<>(List.of(createOffer(requestDto, false, false),
                createOffer(requestDto, false, true),
                createOffer(requestDto, true, false),
                createOffer(requestDto, true, true)));
        offers.sort(Comparator.comparing(LoanOfferDto::getRate));
        Collections.reverse(offers);
        return offers;
    }

    /**
     * Method creating credit offer
     *
     * @param requestDto        - dto contained information about client
     * @param isInsuranceEnable - true, if client takes insurance
     * @param isSalaryClient    - true, if client have a salary client status
     * @return dto with credit offer
     */
    private LoanOfferDto createOffer(LoanStatementRequestDto requestDto,
                                     boolean isInsuranceEnable,
                                     boolean isSalaryClient) {
        //Считаем предлагаемую сумму, учитывая взятие страховки и статус "зарплатного клиента"
        BigDecimal totalAmount = scoringService.calculateTotalAmount(requestDto.getAmount(), isInsuranceEnable, isSalaryClient);
        log.info("Total amount calculated for offer: " + totalAmount);
        //Считаем ставку банка, учитывая взятие страховки и статус "зарплатного клиента"
        BigDecimal rate = scoringService.calculateRate(isInsuranceEnable, isSalaryClient);
        log.info("Rate calculated for offer: " + rate);
        BigDecimal monthlyPayment;
        //Считаем ежемесячный платёж с учётом суммы, ставки банка и срока кредитования
        try {
            monthlyPayment = scoringService.calculateMonthlyPayment(totalAmount, rate, requestDto.getTerm());
        } catch (ArithmeticException ae){
            throw new ArithmeticException("can't create offer because of dividing by zero");
        }
        log.info("Monthly payment for offer calculated: " + monthlyPayment);
        //Возвращаем сформированное предложение по кредиту
        return new LoanOfferDto(UUID.randomUUID(),
                requestDto.getAmount(),
                totalAmount,
                requestDto.getTerm(),
                monthlyPayment==null?null:monthlyPayment.setScale(2, RoundingMode.HALF_EVEN),
                rate,
                isInsuranceEnable,
                isSalaryClient);
    }

    /**
     * Method for creating credit
     *
     * @param scoringDataDto - information about client and requested credit
     * @param scoredRate     - additional rate, calculated in scoring
     * @return credit with monthly payment schedule
     */
    public CreditDto createCredit(ScoringDataDto scoringDataDto, BigDecimal scoredRate) {
        //Считаем общую сумму по кредиту с учётом взятия страховки и статуса "зарплатного клиента"
        BigDecimal totalAmount = scoringService.calculateTotalAmount(scoringDataDto.getAmount(),
                scoringDataDto.getIsInsuranceEnabled(),
                scoringDataDto.getIsSalaryClient());
        log.info("Total amount calculated for credit: " + totalAmount);
        //Считаем банковскую ставку с учётом взятия страховки и статуса "зарплатного клиента"
        BigDecimal rate = scoringService.calculateRate(scoringDataDto.getIsInsuranceEnabled(),
                scoringDataDto.getIsSalaryClient());
        log.info("Rate calculated for credit: " + rate);
        //Добавляем к ставке добавочную ставку, расчитанную в скоринге
        rate = rate.add(scoredRate);
        log.info("Rate from scoring added to rate : " + rate);
        //Считаем ежемесячный платёж клиента по аннуитентному типу платежей
        BigDecimal monthlyPayment;
        try {
            monthlyPayment = scoringService.calculateMonthlyPayment(totalAmount, rate, scoringDataDto.getTerm());
        } catch (ArithmeticException ae){
            throw new ArithmeticException("can't calculate monthlyPayment because of divide by zero");
        }
        log.info("Monthly payment calculated for credit: " + monthlyPayment);
        //Считаем полную стоимость кредита
        BigDecimal psk = scoringService.calculatePSK(monthlyPayment, scoringDataDto.getTerm());
        log.info("PSK calculated for credit: " + psk);
        //Формируем кредитное предложение
        return new CreditDto(totalAmount,
                scoringDataDto.getTerm(),
                monthlyPayment,
                rate,
                psk,
                scoringDataDto.getIsInsuranceEnabled(),
                scoringDataDto.getIsSalaryClient(),
                createPaymentSchedule(1, totalAmount, rate, monthlyPayment)); //Формируем и добавляем график ежемесячных платежей
    }

    /**
     * Method for creating monthly payment schedule
     *
     * @param number         - number of payment in schedule
     * @param amount         - remaining payment amount
     * @param rate           - credit rate
     * @param monthlyPayment - monthly payment
     * @return payment schedule
     */
    private List<PaymentScheduleElementDto> createPaymentSchedule(Integer number, BigDecimal amount,
                                                                  BigDecimal rate,
                                                                  BigDecimal monthlyPayment) {
        //Создаём объект платежа
        PaymentScheduleElementDto paymentScheduleElementDto = new PaymentScheduleElementDto();
        //Устанавливаем значение номера и суммы остатка
        paymentScheduleElementDto.setNumber(number);
        //Считаем ежемесячную ставку
        BigDecimal monthlyRate = rate.divide(new BigDecimal(12), 20, RoundingMode.HALF_EVEN).
                divide(new BigDecimal(100), 20, RoundingMode.HALF_EVEN);
        //Считаем платёж к банку
        BigDecimal interestPayment = amount.multiply(monthlyRate);
        //Считаем платёж по долгу
        BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
        //Считаем оставшийся долг
        BigDecimal remainingDebt = amount.subtract(debtPayment);
        //Заносим данные в наш объект
        paymentScheduleElementDto.setTotalPayment(monthlyPayment);
        paymentScheduleElementDto.setInterestPayment(interestPayment.setScale(2, RoundingMode.HALF_EVEN));
        paymentScheduleElementDto.setDebtPayment(debtPayment.setScale(2, RoundingMode.HALF_UP));
        paymentScheduleElementDto.setRemainingDebt(remainingDebt.setScale(2, RoundingMode.HALF_EVEN));
        //Устанавливаем дату платежа
        LocalDate today = LocalDate.now();
        paymentScheduleElementDto.setDate(today.plusMonths(number - 1));
        log.info("New payment added in schedule" + paymentScheduleElementDto);
        //Создаём лист платежей для записи в него
        List<PaymentScheduleElementDto> paymentSchedule;
        //Пока у нас есть долг по кредиту - уходим в рекурсию с остатком по платежу. Иначе - формируем список с последним платежом
        if ((remainingDebt.compareTo(BigDecimal.ZERO) > 0) && (remainingDebt.compareTo(BigDecimal.ONE) > 0)) {
            paymentSchedule = createPaymentSchedule(++number, remainingDebt, rate, monthlyPayment);
            paymentSchedule.add(paymentScheduleElementDto);
        } else {
            return new ArrayList<>(List.of(paymentScheduleElementDto));
        }
        //Возвращаем сформированный график платежей
        Collections.reverse(paymentSchedule);
        return paymentSchedule;
    }
}
