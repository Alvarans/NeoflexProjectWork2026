package com.example.deal.service.impl;

import com.example.deal.service.KafkaProducerService;
import com.example.dossier.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    public void send(String topic, EmailMessage message) {
        kafkaTemplate.send(topic, message);
    }
}
