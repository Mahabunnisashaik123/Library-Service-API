package com.example.libraryService.exception;

public class InventoryServiceDownException extends RuntimeException {
    public InventoryServiceDownException(String message) {
        super(message);
    }
}
