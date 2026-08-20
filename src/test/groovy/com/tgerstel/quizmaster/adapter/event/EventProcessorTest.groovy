package com.tgerstel.quizmaster.adapter.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.tgerstel.quizmaster.adapter.persistence.EventDocument
import com.tgerstel.quizmaster.adapter.persistence.EventRepository
import com.tgerstel.quizmaster.domain.event.DomainEvent
import com.tgerstel.quizmaster.domain.event.EventStatus
import com.tgerstel.quizmaster.domain.event.EventType
import com.tgerstel.quizmaster.domain.event.QuizCompletedEventPayload
import spock.lang.Specification

import java.time.Instant

class EventProcessorTest extends Specification {

    EventRepository eventRepository = Mock()
    EventHandlerRegistry handlerRegistry = Mock()
    ObjectMapper objectMapper = Mock()
    EventRetryPolicy retryPolicy = Mock()

    EventProcessor eventProcessor

    def setup() {
        eventProcessor = new EventProcessor(
                eventRepository,
                handlerRegistry,
                objectMapper,
                retryPolicy
        )
    }

    def "should do nothing when there are no pending events"() {
        given:
        when:
        eventProcessor.process()

        then:
        1 * eventRepository.claimNextEvent() >> Optional.empty()
        0 * handlerRegistry._
        0 * objectMapper._
        0 * eventRepository.save(_)
    }

    def "should successfully process event"() {
        given:
        def event = eventDocument()
        def payload = Mock(QuizCompletedEventPayload)
        def handler = Mock(EventHandler)

        eventRepository.claimNextEvent() >> Optional.of(event)
        handlerRegistry.getHandler(event.type) >> handler
        objectMapper.readValue(
                event.payload,
                event.type.payloadClass
        ) >> payload

        when:
        eventProcessor.process()

        then:
        1 * handler.handle({
            it.eventId == event.id
            it.type == event.type
            it.occurredAt == event.createdAt
            it.payload == payload
        })

        1 * eventRepository.save({
            it == event
            it.status == EventStatus.COMPLETED
            it.processedAt != null
            it.lastError == null
        })

        and:
        event.status == EventStatus.COMPLETED
        event.processedAt != null
        event.lastError == null
    }

    def "should schedule event for retry when processing fails and retry is allowed"() {
        given:
        def event = eventDocument()
        def exception = new RuntimeException("Handler failed")
        def nextAttemptAt = Instant.parse("2026-08-20T20:00:00Z")

        eventRepository.claimNextEvent() >> Optional.of(event)

        handlerRegistry.getHandler(event.type) >> {
            throw exception
        }

        when:
        eventProcessor.process()

        then:
        1 * retryPolicy.canRetry(event) >> true
        1 * retryPolicy.nextAttemptAt(event.attempts) >> nextAttemptAt

        1 * eventRepository.save({
            it == event
            it.status == EventStatus.PENDING
            it.nextAttemptAt == nextAttemptAt
            it.lastError == "Handler failed"
        })

        and:
        event.status == EventStatus.PENDING
        event.nextAttemptAt == nextAttemptAt
        event.lastError == "Handler failed"
    }

    def "should mark event as failed when retry is not allowed"() {
        given:
        def event = eventDocument()
        def exception = new RuntimeException("Handler failed")

        eventRepository.claimNextEvent() >> Optional.of(event)

        handlerRegistry.getHandler(event.type) >> {
            throw exception
        }

        when:
        eventProcessor.process()

        then:
        1 * retryPolicy.canRetry(event) >> false
        0 * retryPolicy.nextAttemptAt(_)

        1 * eventRepository.save({
            it == event
            it.status == EventStatus.FAILED
            it.lastError == "Handler failed"
        })

        and:
        event.status == EventStatus.FAILED
        event.lastError == "Handler failed"
    }

    def "should schedule retry when handler throws exception"() {
        given:
        def event = eventDocument()
        def payload = Mock(QuizCompletedEventPayload)
        def handler = Mock(EventHandler)
        def exception = new RuntimeException("Email service unavailable")
        def nextAttemptAt = Instant.parse("2026-08-20T20:00:00Z")

        eventRepository.claimNextEvent() >> Optional.of(event)
        handlerRegistry.getHandler(event.type) >> handler
        objectMapper.readValue(_, _) >> payload
        retryPolicy.canRetry(event) >> true
        retryPolicy.nextAttemptAt(event.attempts) >> nextAttemptAt

        when:
        eventProcessor.process()

        then:
        1 * handler.handle(_) >> {
            throw exception
        }

        1 * eventRepository.save({
            it.status == EventStatus.PENDING
            it.nextAttemptAt == nextAttemptAt
            it.lastError == "Email service unavailable"
        })
    }

    private static EventDocument eventDocument() {
        def event = new EventDocument()
        event.id = UUID.randomUUID()
        event.type = EventType.QUIZ_COMPLETED
        event.payload = '{"attemptId":"abc"}'
        event.status = EventStatus.PENDING
        event.attempts = 1
        event.createdAt = Instant.parse("2026-08-20T18:00:00Z")
        event
    }
}
