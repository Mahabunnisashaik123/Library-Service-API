package com.example.libraryService.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibraryKafkaProducer {
@Autowired
    private KafkaTemplate<String, String> stringKafkaTemplate;
@Autowired
    private KafkaTemplate<String, BookKafkaEvent> jsonKafkaTemplate;

    public void sendStringEvent(String message) {
        log.info("Publishing string message to topic '{}': {}", KafkaTopics.EVENTS, message);
        stringKafkaTemplate.send(KafkaTopics.EVENTS, message);
    }

    public void sendJsonEvent(BookKafkaEvent event) {
        log.info("Publishing JSON event to topic '{}': {}", KafkaTopics.BOOKS, event);
        jsonKafkaTemplate.send(KafkaTopics.BOOKS, event);
    }
}
