package com.example.dossier.service.impl;

import com.example.api.common.dto.EmailMessage;
import com.example.dossier.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Сервис отправки сообщений на почту
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    @Value("${services.deal.url}")
    String dealUrl;

    /**
     * Формирование сообщения об необходимости завершить регистрацию
     *
     * @param msg Объект сообщения для получения данных о получателе
     */
    public void sendFinishRegistration(EmailMessage msg) {
        String subject = "Завершите регистрацию";
        String text = "Перейдите по ссылке для завершения регистрации";

        send(msg.getAddress(), subject, text, msg.getStatementId());
    }

    /**
     * Формирование сообщения со ссылкой на документы, которые необходимо заполнить
     *
     * @param msg Объект сообщения для получения данных о получателе
     */
    public void createDocuments(EmailMessage msg) {
        String subject = "Ваши документы готовы к заполнению";
        String text = "Необходимо сформировать документы по ссылке: "
                + dealUrl
                + msg.getStatementId() + "/send"
                + "\n Ссылка прикреплена для примера. По умолчанию, с почты идут get запросы."
                + "\n Поэтому, ссылка должна вести на фронт(которого нет)";

        send(msg.getAddress(), subject, text, msg.getStatementId());
    }

    /**
     * Формирование сообщения со ссылкой на подписание
     *
     * @param msg Объект сообщения для получения данных о получателе
     */
    public void sendDocuments(EmailMessage msg) {
        String subject = "Ваши документы готовы";
        String text = "Документы сформированы и доступны для подписания!"
                + "\n Пожалуйста, пройдите по ссылке для подписания: "
                + dealUrl
                + msg.getStatementId() + "/sing"
                + "\n Ссылка прикреплена для примера. По умолчанию, с почты идут get запросы."
                + "\n Поэтому, ссылка должна вести на фронт(которого нет)";

        send(msg.getAddress(), subject, text, msg.getStatementId());
    }

    /**
     * Формирование письма с кодом подтверждения
     *
     * @param msg Объект сообщения для получения данных о получателе
     */
    public void sendSesCode(EmailMessage msg) {
        String subject = "Код подтверждения";
        String sesCode = msg.getText();
        String text = "Ваш код: " + sesCode
                + "\nДля подтверждения введите полученный код по ссылке: "
                + dealUrl
                + msg.getStatementId() + "/code"
                + "\n Ссылка прикреплена для примера. По умолчанию, с почты идут get запросы."
                + "\n Поэтому, ссылка должна вести на фронт(которого нет)";

        send(msg.getAddress(), subject, text, msg.getStatementId());
    }

    /**
     * Формирование сообщения об успешном оформлении кредита
     *
     * @param msg Объект сообщения для получения данных о получателе
     */
    public void sendCreditIssued(EmailMessage msg) {
        String subject = "Кредит выдан";
        String text = "Поздравляем! Ваш кредит успешно оформлен";

        send(msg.getAddress(), subject, text, msg.getStatementId());
    }

    /**
     * Формирование сообщения об отказе по заявке
     *
     * @param msg Объект сообщения для получения данных о получателе
     */
    public void sendDenied(EmailMessage msg) {
        String subject = "Отказ по заявке";
        String text = "К сожалению, заявка отклонена";

        send(msg.getAddress(), subject, text, msg.getStatementId());
    }

    /**
     * Отправление сформированного сообщения клиенту
     *
     * @param to          Почта отправления сообщения
     * @param subject     Тема сообщения
     * @param text        Текст сообщения
     * @param statementId Идентификационный номер заявки
     */
    private void send(String to, String subject, String text, UUID statementId) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(text);

            mailSender.send(mail);

            log.info("Письмо отправлено: statementId={}, address={}, subject={}, text={}"
                    , statementId, to, subject, text);

        } catch (Exception e) {
            log.error("Ошибка отправки письма: statementId={}", statementId, e);
            throw new IllegalArgumentException(e);
        }
    }
}
