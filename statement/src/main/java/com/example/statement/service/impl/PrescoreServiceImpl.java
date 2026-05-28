package com.example.statement.service.impl;

import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.statement.service.PrescoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

/**
 * Сервис, реализующий первичную проверку данных перед получением предложений
 */
@Service
@Slf4j
public class PrescoreServiceImpl implements PrescoreService {
    /**
     * Method for prescoring request dto according to certain rules
     *
     * @param requestDto - requested dto. Contains information about client
     */
    public void prescore(LoanStatementRequestDto requestDto) {
        //Проверка на то, состоит ли имя из латинских символов или нет
        if (!(isLatina(requestDto.getFirstName()))) {
            log.error("You must use letters in first name");
            throw new IllegalArgumentException("You must use letters in first name");
        }
        //Проверка на то, состоит ли фамилия из латинских символов или нет
        if (!(isLatina(requestDto.getLastName()))) {
            log.error("You must use letters in last name");
            throw new IllegalArgumentException("You must use letters in last name");
        }
        String middleName = requestDto.getMiddleName();
        //Проверка отчества на длину и содержание латинских букв при его наличии
        if (middleName != null) {
            if ((middleName.length() < 2) || (middleName.length() > 30)) {
                log.error("Your middlename is uncorrect lenght");
                throw new IllegalArgumentException("Your middlename is uncorrect lenght");
            }
            if (!(isLatina(requestDto.getMiddleName()))) {
                log.error("You must use letters in middle name");
                throw new IllegalArgumentException("You must use letters in middle name");
            }
        }
        //Проверка клиента на совершеннолетие
        if (Period.between(requestDto.getBirthdate(), LocalDate.now())
                .getYears() < 18) {
            log.error("Your age must be more then 18");
            throw new IllegalArgumentException("Your age must be more than 18");
        }
        log.info("Prescore success");
    }

    private boolean isLatina(String word) {
        return word.matches("^[A-Za-z]+$");
    }
}
