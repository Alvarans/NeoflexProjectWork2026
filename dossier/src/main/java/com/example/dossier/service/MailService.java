package com.example.dossier.service;

import com.example.dossier.dto.EmailMessage;

public interface MailService {
    void sendFinishRegistration(EmailMessage msg);
    void createDocuments(EmailMessage msg);
    void sendDocuments(EmailMessage msg);
    void sendSesCode(EmailMessage msg);
    void sendCreditIssued(EmailMessage msg);
    void sendDenied(EmailMessage msg);
}

