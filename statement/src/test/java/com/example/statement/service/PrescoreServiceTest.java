package com.example.statement.service;

import com.example.api.common.dto.LoanStatementRequestDto;
import com.example.statement.service.impl.PrescoreServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PrescoreServiceTest {

    private final PrescoreServiceImpl prescoreService = new PrescoreServiceImpl();

    @Test
    void shouldPassPrescoreForValidRequest() {
        LoanStatementRequestDto dto = validDto();

        assertDoesNotThrow(() -> prescoreService.prescore(dto));
    }

    @Test
    void shouldThrowWhenFirstNameIsNotLatin() {
        LoanStatementRequestDto dto = validDto();
        dto.setFirstName("Иван");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> prescoreService.prescore(dto)
        );

        assertEquals("You must use letters in first name", ex.getMessage());
    }

    @Test
    void shouldThrowWhenLastNameIsNotLatin() {
        LoanStatementRequestDto dto = validDto();
        dto.setLastName("Иванов");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> prescoreService.prescore(dto)
        );

        assertEquals("You must use letters in last name", ex.getMessage());
    }

    @Test
    void shouldThrowWhenMiddleNameTooShort() {
        LoanStatementRequestDto dto = validDto();
        dto.setMiddleName("A");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> prescoreService.prescore(dto)
        );

        assertEquals("Your middlename is uncorrect lenght", ex.getMessage());
    }

    @Test
    void shouldThrowWhenMiddleNameNotLatin() {
        LoanStatementRequestDto dto = validDto();
        dto.setMiddleName("Иваныч");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> prescoreService.prescore(dto)
        );

        assertEquals("You must use letters in middle name", ex.getMessage());
    }

    @Test
    void shouldThrowWhenAgeLessThan18() {
        LoanStatementRequestDto dto = validDto();
        dto.setBirthdate(LocalDate.now().minusYears(17));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> prescoreService.prescore(dto)
        );

        assertEquals("Your age must be more than 18", ex.getMessage());
    }

    private LoanStatementRequestDto validDto() {
        LoanStatementRequestDto dto = new LoanStatementRequestDto();
        dto.setFirstName("Ivan");
        dto.setLastName("Ivanov");
        dto.setMiddleName("Ivanovich");
        dto.setBirthdate(LocalDate.now().minusYears(25));
        return dto;
    }
}
