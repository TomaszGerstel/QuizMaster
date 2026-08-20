package com.tgerstel.quizmaster.adapter.event;

import com.tgerstel.quizmaster.adapter.email.QuizResultEmailTemplate;
import com.tgerstel.quizmaster.domain.event.DomainEvent;
import com.tgerstel.quizmaster.domain.event.EventType;
import com.tgerstel.quizmaster.domain.event.QuizCompletedEventPayload;
import com.tgerstel.quizmaster.domain.exception.AttemptNotFoundException;
import com.tgerstel.quizmaster.domain.port.EmailService;
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuizCompletedEventHandler implements EventHandler<QuizCompletedEventPayload> {

    private final EmailService emailService;
    private final QuizAttemptRepository quizAttemptRepository;

    @Override
    public EventType eventType() {
        return EventType.QUIZ_COMPLETED;
    }

    @Override
    public void handle(DomainEvent<QuizCompletedEventPayload> event) {

        log.info("Handling QuizCompletedEvent: {}", event);

        QuizCompletedEventPayload payload = event.getPayload();

        if (payload.getRecipientEmail() == null || payload.getRecipientEmail().isEmpty()) {
            log.warn("Recipient email is missing for QuizCompletedEvent: {}", event);
            return;
        }

        var quizAttempt = quizAttemptRepository.findBySessionId(payload.getAttemptId());

        if (quizAttempt.isEmpty()) {
            log.warn("Quiz attempt not found for sessionId: {}", payload.getAttemptId());
            throw new AttemptNotFoundException(payload.getAttemptId());
        }

        var subject = quizAttempt.get().getIsPassed()
                ? "Quiz Master - quiz passed"
                : "Quiz Master - quiz result";

        var html = QuizResultEmailTemplate.create(quizAttempt.get());

        emailService.send(
                payload.getRecipientEmail(),
                subject,
                html
        );
    }
}