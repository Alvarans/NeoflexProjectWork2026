package com.example.statement;

import com.example.statement.handler.ControllerExceptionHandler;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatementControllerExceptionHandlerTest {

    private ControllerExceptionHandler handler;

    @Mock
    private MethodArgumentNotValidException validationException;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpMessageNotReadableException readableException;

    @BeforeEach
    void setUp() {
        handler = new ControllerExceptionHandler();
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException ex =
                new IllegalArgumentException("Invalid amount");

        ResponseEntity<String> response =
                handler.illegalArgument(ex, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid amount", response.getBody());
    }

    @Test
    void shouldHandleValidationExceptions() {
        FieldError amountError =
                new FieldError("loanDto", "amount", "must not be null");

        FieldError termError =
                new FieldError("loanDto", "term", "must be positive");

        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors())
                .thenReturn(List.of(amountError, termError));

        Map<String, String> response =
                handler.handleValidationExceptions(validationException);

        assertEquals(2, response.size());
        assertEquals("must not be null", response.get("amount"));
        assertEquals("must be positive", response.get("term"));
    }

    @Test
    void shouldHandleReadableException() {
        RuntimeException cause =
                new RuntimeException("Unexpected character");

        when(readableException.getMostSpecificCause()).thenReturn(cause);
        when(readableException.getMessage()).thenReturn("Malformed JSON");

        Map<String, String> response =
                handler.handleReadableException(readableException);

        assertEquals("Malformed JSON request or missing required fields",
                response.get("error"));
        assertEquals("Unexpected character",
                response.get("details"));
    }

    @Test
    void shouldHandleNullPointerException() {
        NullPointerException ex =
                new NullPointerException("Statement not found");

        Map<String, String> response =
                handler.handleNullPointer(ex);

        assertEquals("Data not found or reference is null",
                response.get("error"));
        assertEquals("Statement not found",
                response.get("message"));
    }

    @Test
    void shouldHandleHttpClientErrorException() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad request",
                null,
                "Your age must be more than 18".getBytes(),
                StandardCharsets.UTF_8
        );

        ResponseEntity<Map<String, Object>> response =
                handler.handleHttpClientException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Error from External Service",
                response.getBody().get("error"));
        assertEquals("Your age must be more than 18",
                response.getBody().get("details"));
    }

    @Test
    void shouldHandleFeignException() {
        Request request = Request.create(
                Request.HttpMethod.POST,
                "/statement/offer",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null
        );

        FeignException ex = FeignException.errorStatus(
                "selectOffer",
                feign.Response.builder()
                        .status(422)
                        .reason("Unprocessable Entity")
                        .request(request)
                        .body("Offer already selected", StandardCharsets.UTF_8)
                        .build()
        );

        ResponseEntity<Map<String, Object>> response =
                handler.handleFeignException(ex);

        assertEquals(422, response.getStatusCode().value());
        assertEquals("Error from External Service",
                response.getBody().get("error"));
        assertEquals("Offer already selected",
                response.getBody().get("message"));
        assertEquals(422,
                response.getBody().get("status"));
    }

    @Test
    void shouldHandleFeignExceptionWithNegativeStatus() {
        // Эмулируем исключение Feign без HTTP-статуса (например, ошибка соединения)
        feign.FeignException ex = feign.FeignException.errorStatus(
                "someMethod",
                feign.Response.builder()
                        .status(-1) // Статус -1
                        .reason("Connection refused")
                        .request(Request.create(Request.HttpMethod.GET, "/url", Collections.emptyMap(), null, StandardCharsets.UTF_8, null))
                        .build()
        );

        ResponseEntity<Map<String, Object>> response =
                handler.handleFeignException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(-1, response.getBody().get("status"));
        assertEquals("Error from External Service", response.getBody().get("error"));
    }
}
