package com.tgerstel.quizmaster.adapter.email;

import com.tgerstel.quizmaster.domain.exception.EmailSendingException;
import com.tgerstel.quizmaster.domain.port.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

@Service
@RequiredArgsConstructor
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void send(String recipient, String subject, String htmlContent) {

        try {
            var message = mailSender.createMimeMessage();

            var helper = new MimeMessageHelper(
                    message,
                    true,
                    "UTF-8"
            );

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException | MailException e) {
            throw new EmailSendingException("Failed to send email to " + recipient, e);
        }
    }
}
