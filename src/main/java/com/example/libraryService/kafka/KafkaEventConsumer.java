package com.example.libraryService.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaEventConsumer {

    @KafkaListener(topics = KafkaTopics.BOOKS, groupId = "library-group")
    public void consumeBooks(String message) {
        log.info("Consumed message from BOOKS topic: {}", message);
    }

    @KafkaListener(topics = KafkaTopics.EVENTS, groupId = "library-group")
    public void consumeEvents(String message) {
        log.info("Consumed message from EVENTS topic: {}", message);
    }
}
