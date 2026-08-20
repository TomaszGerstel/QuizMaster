package com.tgerstel.quizmaster.adapter.event

import com.tgerstel.quizmaster.domain.event.DomainEvent
import com.tgerstel.quizmaster.domain.event.EventType
import com.tgerstel.quizmaster.domain.event.QuizCompletedEventPayload
import com.tgerstel.quizmaster.domain.exception.AttemptNotFoundException
import com.tgerstel.quizmaster.domain.model.QuizAttempt
import com.tgerstel.quizmaster.domain.port.EmailService
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository
import spock.lang.Specification

import java.time.Instant

class QuizCompletedEventHandlerTest extends Specification {

    QuizAttemptRepository quizAttemptRepository
    EmailService emailService
    QuizCompletedEventHandler handler

    def setup() {
        quizAttemptRepository = Mock()
        emailService = Mock()
        handler = new QuizCompletedEventHandler(emailService, quizAttemptRepository)
    }

    def "should not send email when recipient email is missing"() {
        given:
        def payload = payload()
        payload.recipientEmail = null

        def event = event(payload)

        when:
        handler.handle(event)

        then:
        0 * quizAttemptRepository._
        0 * emailService._
    }

    def "should throw exception when quiz attempt does not exist"() {
        given:
        def payload = payload()
        def event = event(payload)

        quizAttemptRepository.findBySessionId(payload.attemptId) >> Optional.empty()

        when:
        handler.handle(event)

        then:
        def exception = thrown(AttemptNotFoundException)
        exception.message.contains(payload.attemptId)

        0 * emailService._
    }

    def "should send email when quiz attempt exists and recipient email is present"() {
        given:
        def payload = payload()
        def event = event(payload)

        def quizAttempt = Mock(QuizAttempt)
        quizAttemptRepository.findBySessionId(payload.attemptId) >> Optional.of(quizAttempt)
        quizAttempt.getQuizName() >> "Sample Quiz"
        quizAttempt.getIsPassed() >> true
        quizAttempt.getCorrectAnswers() >> 8
        quizAttempt.getQuestionsCount() >> 10
        quizAttempt.getPassRate() >> 80
        quizAttempt.getQuizDurationSeconds() >> 300

        when:
        handler.handle(event)

        then:
        1 * emailService.send(
                payload.recipientEmail,
                "Quiz Master - quiz passed",
                _ as String
        )
    }

    def payload() {
        return new QuizCompletedEventPayload(
                "attemptId",
                "quizId",
                "email@1"
        )
    }

    def event(QuizCompletedEventPayload payload) {
        return new DomainEvent<QuizCompletedEventPayload>(
                UUID.randomUUID(),
                EventType.QUIZ_COMPLETED,
                Instant.now(),
                payload
        )
    }
}
