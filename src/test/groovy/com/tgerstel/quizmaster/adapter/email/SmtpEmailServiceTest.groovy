package com.tgerstel.quizmaster.adapter.email

import com.tgerstel.quizmaster.domain.exception.EmailSendingException
import jakarta.mail.Message
import jakarta.mail.Session
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import spock.lang.Specification

class SmtpEmailServiceTest extends Specification {

    JavaMailSender mailSender = Mock()

    SmtpEmailService smtpEmailService

    def setup() {
        smtpEmailService = new SmtpEmailService(mailSender)
    }

    def "should send html email with correct content"() {
        given:
        def message = new MimeMessage(Session.getInstance(new Properties()))

        mailSender.createMimeMessage() >> message

        def html = """
        <h1>Quiz completed</h1>
        <p>Score: 85%</p>
        """

        when:
        smtpEmailService.send(
                "test@example.com",
                "Quiz completed",
                html
        )

        then:
        1 * mailSender.send(message)

        and:
        message.getRecipients(Message.RecipientType.TO)*.toString() == [
                "test@example.com"
        ]

        and:
        message.writeTo(System.out)

        message.subject == "Quiz completed"

        and:
        def actualHtml = extractHtml(message)

        actualHtml.contains("Quiz completed")
        actualHtml.contains("Score: 85%")
    }

    def "should throw EmailSendingException when email cannot be sent"() {
        given:
        def message = new MimeMessage(Session.getInstance(new Properties()))

        mailSender.createMimeMessage() >> message
        mailSender.send(message) >> { throw new MailSendException("SMTP unavailable") }

        when:
        smtpEmailService.send(
                "test@example.com",
                "Quiz completed",
                "<h1>Quiz completed</h1>"
        )

        then:
        def exception = thrown(EmailSendingException)

        exception.message == "Failed to send email to test@example.com"
        exception.cause instanceof MailSendException
    }

    private static String extractHtml(MimeMessage message) {
        def mixed = message.content as MimeMultipart
        def related = mixed.getBodyPart(0).content as MimeMultipart
        def htmlPart = related.getBodyPart(0)
        return htmlPart.content as String
    }

}