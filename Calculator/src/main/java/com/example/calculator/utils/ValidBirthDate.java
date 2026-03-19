package com.example.calculator.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BirthDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBirthDate {
    String message() default "Дата рождения должна быть не позднее 18 лет с текущего дня";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
