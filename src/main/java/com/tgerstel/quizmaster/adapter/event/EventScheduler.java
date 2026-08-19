package com.tgerstel.quizmaster.adapter.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventScheduler {

    private final EventProcessor eventProcessor;

    @Scheduled(fixedDelay = 10000)
    public void processEvents() {
        log.info("Scheduled event processing started.");
        eventProcessor.process();
    }
}
