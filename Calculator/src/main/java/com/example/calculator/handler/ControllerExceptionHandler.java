package com.example.calculator.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice

public class ControllerExceptionHandler {
    Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * Handler for arithmetic exception
     * @param ex - exception object
     * @param request - request for client
     * @return status bad_request with exception message
     */
    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<String> divideByZero(ArithmeticException ex, WebRequest request) {
        logger.error("Arithmetic exception handled: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgument(IllegalArgumentException iae, WebRequest request){
        logger.error("Illegal argument exception handled: " + iae.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(iae.getMessage());
    }
    /**
     * Method, who handle ArgumentNotValidException exception
     *
     * @param ex - body of exception
     * @return map of errors, contains naming of field, failed validation, and default message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            logger.error("Validation error. In field " + fieldName + " with message: " + errorMessage);
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
