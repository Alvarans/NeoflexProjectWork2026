package com.example.statement.service;

import com.example.api.common.dto.LoanStatementRequestDto;

/**
 * Сервис, предоставляющий услуги первичной проверки данных на корректность
 */
public interface PrescoreService {
    /**
     * Method for prescoring request dto according to certain rules
     *
     * @param requestDto - requested dto. Contains information about client
     */
    void prescore(LoanStatementRequestDto requestDto);
}
