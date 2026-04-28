package com.example.deal.service;

import com.example.dossier.dto.EmailMessage;


public interface KafkaProducerService {
    void send(String topic, EmailMessage message);
}
