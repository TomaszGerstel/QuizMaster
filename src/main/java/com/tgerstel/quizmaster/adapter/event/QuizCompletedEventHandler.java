package com.tgerstel.quizmaster.adapter.event;

import com.tgerstel.quizmaster.domain.event.DomainEvent;
import com.tgerstel.quizmaster.domain.event.EventType;
import com.tgerstel.quizmaster.domain.event.QuizCompletedEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuizCompletedEventHandler implements EventHandler<QuizCompletedEventPayload> {

//    private final EmailService emailService;

    @Override
    public EventType eventType() {
        return EventType.QUIZ_COMPLETED;
    }

    @Override
    public void handle(DomainEvent<QuizCompletedEventPayload> event) {

        log.info("Handling QuizCompletedEvent: {}", event);

        QuizCompletedEventPayload payload = event.getPayload();

//        emailService.sendQuizResult(
//                payload.getRecipientEmail(),
//                payload.getQuizId(),
//                payload.getAttemptId()
//        );
    }
}