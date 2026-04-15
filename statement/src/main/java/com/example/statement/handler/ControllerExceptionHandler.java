package com.example.statement.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerExceptionHandler {
    Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<String, String> handleReadableException(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Malformed JSON request or missing required fields");
        errors.put("details", ex.getMostSpecificCause().getMessage());
        logger.error("JSON parsing error: " + ex.getMessage());
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    public Map<String, String> handleNullPointer(NullPointerException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Data not found or reference is null");
        error.put("message", ex.getMessage());
        logger.error("NPE Exception: ", ex);
        return error;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Map<String, Object>> handleHttpClientException(HttpClientErrorException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Validation Error from External Service");
        // Извлекаем само сообщение "Your age must be more than 18"
        body.put("details", ex.getResponseBodyAsString());

        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignException(feign.FeignException ex) {
        Map<String, Object> body = new HashMap<>();
        String responseBody = ex.contentUTF8();
        body.put("error", "Error from External Service");
        body.put("message", responseBody.isEmpty() ? ex.getMessage() : responseBody);
        body.put("status", ex.status());
        return ResponseEntity
                .status(ex.status() > 0 ? ex.status() : 500)
                .body(body);
    }
}
