package com.example.calculator.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.Period;

@Validated
public class BirthDateValidator implements ConstraintValidator<ValidBirthDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        Period age = Period.between(localDate,LocalDate.now());
        return age.getYears() >= 18;
    }
}
