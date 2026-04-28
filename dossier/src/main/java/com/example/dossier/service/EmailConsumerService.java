package com.example.dossier.service;

import com.example.dossier.dto.EmailMessage;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;


public interface EmailConsumerService {

   void handle(EmailMessage message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic);

}