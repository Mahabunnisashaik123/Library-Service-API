package com.example.libraryService.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookKafkaEvent {
    private Long id;
    private String title;
    private String author;
    private double price;
    private int stock;
    private String action;
    private String timestamp;
}
