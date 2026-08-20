package com.tgerstel.quizmaster.adapter.event

import com.tgerstel.quizmaster.adapter.persistence.EventDocument
import spock.lang.Specification

import java.time.Instant

class EventRetryPolicyTest extends Specification {

    EventRetryPolicy retryPolicy = new EventRetryPolicy()

    def "should allow retry when attempts are below maximum"() {
        given:
        def event = new EventDocument()
        event.attempts = attempts

        expect:
        retryPolicy.canRetry(event) == expected

        where:
        attempts || expected
        0        || true
        1        || true
        2        || true
        3        || true
        4        || true
        5        || false
        6        || false
    }

    def "should calculate delay for attempt"() {
        given:
        def before = Instant.now()

        when:
        def result = retryPolicy.nextAttemptAt(attempts)

        then:
        def after = Instant.now().plusSeconds(expectedSeconds)

        result >= before.plusSeconds(expectedSeconds)
        result <= after

        where:
        attempts || expectedSeconds
        1        || 30
        2        || 60
        3        || 300
        4        || 900
        5        || 0
    }
}
