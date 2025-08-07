package com.example.libraryService.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomDateTimeFormatter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private CustomDateTimeFormatter() {}

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
