package com.tgerstel.quizmaster.adapter.event

import com.tgerstel.quizmaster.domain.event.EventType
import spock.lang.Ignore
import spock.lang.Specification

class EventHandlerRegistryTest extends Specification {

    def "should return handler for event type"() {
        given:
        def handler = Mock(EventHandler) {
            eventType() >> EventType.QUIZ_COMPLETED
        }

        def registry = new EventHandlerRegistry([handler])

        expect:
        registry.getHandler(EventType.QUIZ_COMPLETED).is(handler)
    }

    @Ignore("There is event type that does not have a handler")
    def "should throw exception when handler does not exist"() {
        given:
        def handler = Mock(EventHandler) {
            eventType() >> EventType.QUIZ_COMPLETED
        }

        def registry = new EventHandlerRegistry([handler])

        when:
        registry.getHandler(EventType.SOME_OTHER_TYPE)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "No handler found for event type: SOME_OTHER_TYPE"
    }
}
