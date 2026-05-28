package com.example.dossier.service.impl;

import com.example.api.common.dto.EmailMessage;
import com.example.dossier.service.EmailConsumerService;
import com.example.dossier.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailConsumerServiceImpl implements EmailConsumerService {
    private final MailService emailService;

    @KafkaListener(
            topics = {
                    "finish-registration",
                    "create-documents",
                    "send-documents",
                    "send-ses",
                    "credit-issued",
                    "statement-denied"
            },
            groupId = "dossier-group"
    )
    public void handle(EmailMessage message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Topic: {}, message: {}", topic, message);
        process(topic, message);
    }

    /**
     * Обработка события в зависимости от топика
     * @param topic топик события
     * @param message объект сообщения
     */
    private void process(String topic, EmailMessage message) {
        switch (topic) {
            case "finish-registration" -> emailService.sendFinishRegistration(message);
            case "create-documents" -> emailService.createDocuments(message);
            case "send-ses" -> emailService.sendSesCode(message);
            case "send-documents" -> emailService.sendDocuments(message);
            case "credit-issued" -> emailService.sendCreditIssued(message);
            case "statement-denied" -> emailService.sendDenied(message);
            default -> emailService.sendDocuments(message);
        }
    }
}
