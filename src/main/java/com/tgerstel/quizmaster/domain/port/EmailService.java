package com.tgerstel.quizmaster.domain.port;

public interface EmailService {

    void send(
            String recipient,
            String subject,
            String htmlContent
    );
}