package com.example.calculator.utils;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LoggingFilter.class);

    private static final int MAX_PAYLOAD_LENGTH = 1000;
    private static final int CACHE_LIMIT = 1024 * 1024;

    private String getStringValue(byte[] content, String encoding) {

        if (content == null || content.length == 0) {
            return "";
        }

        try {
            String payload = new String(content, encoding);

            if (payload.length() > MAX_PAYLOAD_LENGTH) {
                payload = payload.substring(0, MAX_PAYLOAD_LENGTH) + "...";
            }

            payload = maskSensitiveData(payload);

            return payload;

        } catch (UnsupportedEncodingException e) {

            LOGGER.warn("Unsupported encoding: {}", encoding);

            return "";
        }
    }

    private String maskSensitiveData(String payload) {

        return payload
                .replaceAll("(?i)\"password\"\\s*:\\s*\"[^\"]*+\"", "\"password\":\"***\"")
                .replaceAll("(?i)\"token\"\\s*:\\s*\"[^\"]*+\"", "\"token\":\"***\"")
                .replaceAll("(?i)\"accessToken\"\\s*:\\s*\"[^\"]*+\"", "\"accessToken\":\"***\"");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper =
                new ContentCachingRequestWrapper(request, CACHE_LIMIT);

        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {

            filterChain.doFilter(requestWrapper, responseWrapper);

        } finally {

            long duration = System.currentTimeMillis() - startTime;

            String requestBody = getStringValue(
                    requestWrapper.getContentAsByteArray(),
                    request.getCharacterEncoding()
            );

            String responseBody = getStringValue(
                    responseWrapper.getContentAsByteArray(),
                    response.getCharacterEncoding()
            );

            LOGGER.info(
                    "HTTP {} {} status={} duration={}ms requestBody={} responseBody={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    requestBody,
                    responseBody
            );

            responseWrapper.copyBodyToResponse();
        }
    }
}
