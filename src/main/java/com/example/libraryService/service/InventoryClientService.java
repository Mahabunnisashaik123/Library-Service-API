package com.example.libraryService.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class InventoryClientService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String INVENTORY_SERVICE_URL = "http://localhost:8087/api/products";

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallback")
    @Retry(name = "inventoryService")
    @TimeLimiter(name = "inventoryService")
    public CompletableFuture<String> getAllProducts() {
        log.info("🔁 Calling InventoryService...");
        return CompletableFuture.supplyAsync(() ->
            restTemplate.getForObject(INVENTORY_SERVICE_URL, String.class)
        );
    }

    public CompletableFuture<String> fallback(Throwable t) {
        log.error("❌ Inventory Service is down. Fallback activated. Reason: {}", t.getMessage());
        return CompletableFuture.completedFuture("⚠️ Inventory Service is temporarily unavailable. Please try later.");
    }
}
