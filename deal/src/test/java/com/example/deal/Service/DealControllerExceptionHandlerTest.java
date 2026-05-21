package com.example.deal.Service;

import com.example.deal.controller.DealController;
import com.example.deal.handler.ControllerExceptionHandler;
import com.example.deal.service.impl.DealServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DealControllerExceptionHandlerTest {
    private MockMvc mockMvc;
    @Mock
    private DealServiceImpl dealServiceImpl;
    @InjectMocks
    private DealController dealController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(dealController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void testCreateStatement_ThrowsIllegalArgument() throws Exception {
        when(dealServiceImpl.makeADealStatement(any()))
                .thenThrow(new IllegalArgumentException("Invalid client data"));

        String validJson = """
        {
          "firstName": "Ivan",
          "lastName": "Ivanov",
          "email": "test@test.com",
          "birthdate": "2000-01-01",
          "passportSeries": "1234",
          "passportNumber": "123456",
          "amount": 100000,
          "term": 12
        }
        """;

        mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid client data"));
    }

    @Test
    void testCreateStatement_ValidationFailed() throws Exception {

        String invalidJson = """
    {
      "amount": 1000,
      "term": 1,
      "firstName": "",
      "lastName": "",
      "email": "wrong-email",
      "birthdate": "2020-01-01",
      "passportSeries": "12",
      "passportNumber": "123"
    }
    """;

        mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testHandleReadableException() throws Exception {
        String malformedJson = "{ \"firstName\": \"Ivan\" ";

        mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed JSON request or missing required fields"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void testHandleHttpClientException() throws Exception {
        String errorMsg = "Your age must be more then 18";
        HttpClientErrorException exception = new HttpClientErrorException(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                errorMsg.getBytes(),
                StandardCharsets.UTF_8
        );

        when(dealServiceImpl.makeADealStatement(any())).thenThrow(exception);

        String validJson = """
        {
          "firstName": "Ivan",
          "lastName": "Ivanov",
          "email": "test@test.com",
          "birthdate": "2000-01-01",
          "passportSeries": "1234",
          "passportNumber": "123456",
          "amount": 100000,
          "term": 12
        }
        """;

        mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error from External Service"))
                .andExpect(jsonPath("$.details").value(errorMsg));
    }
}