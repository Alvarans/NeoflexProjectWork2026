package com.example.apigateway;

import com.example.apigateway.handler.ControllerExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ControllerExceptionHandlerTest {

    private ControllerExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ControllerExceptionHandler();
    }

    @Test
    void illegalArgument_shouldReturnBadRequest() {

        IllegalArgumentException ex =
                new IllegalArgumentException("Wrong data");

        WebRequest request = mock(WebRequest.class);

        ResponseEntity<String> response =
                handler.illegalArgument(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Wrong data", response.getBody());
    }

    @Test
    void handleValidationExceptions_shouldReturnValidationErrorsMap()
            throws NoSuchMethodException {

        TestDto testDto = new TestDto();

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(testDto, "testDto");

        bindingResult.addError(
                new FieldError(
                        "testDto",
                        "email",
                        "Email is invalid"
                )
        );

        Method method = this.getClass()
                .getDeclaredMethod("dummyMethod", TestDto.class);

        MethodParameter parameter =
                new MethodParameter(method, 0);

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(
                        parameter,
                        bindingResult
                );

        Map<String, String> errors =
                handler.handleValidationExceptions(ex);

        assertEquals(1, errors.size());
        assertEquals(
                "Email is invalid",
                errors.get("email")
        );
    }

    @Test
    void handleReadableException_shouldReturnErrorMap() {

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException(
                        "Malformed JSON",
                        mock(HttpInputMessage.class)
                );

        Map<String, String> response =
                handler.handleReadableException(ex);

        assertEquals(
                "Malformed JSON request or missing required fields",
                response.get("error")
        );

        assertNotNull(response.get("details"));
    }

    @Test
    void handleNullPointer_shouldReturnErrorMap() {

        NullPointerException ex =
                new NullPointerException("Object is null");

        Map<String, String> response =
                handler.handleNullPointer(ex);

        assertEquals(
                "Data not found or reference is null",
                response.get("error")
        );

        assertEquals(
                "Object is null",
                response.get("message")
        );
    }

    @Test
    void handleHttpClientException_shouldReturnResponseEntity() {

        HttpClientErrorException ex =
                HttpClientErrorException.create(
                        HttpStatus.BAD_REQUEST,
                        "Bad Request",
                        null,
                        "Your age must be more than 18".getBytes(),
                        null
                );

        ResponseEntity<Map<String, Object>> response =
                handler.handleHttpClientException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertEquals(
                "Validation Error from External Service",
                response.getBody().get("error")
        );

        assertEquals(
                "Your age must be more than 18",
                response.getBody().get("details")
        );
    }

    private void dummyMethod(TestDto dto) {
    }

    private static class TestDto {
    }
}

