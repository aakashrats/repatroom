package com.repatroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health Controller
 * Basic health check endpoint for monitoring
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    @Operation(summary = "API health check",
            description = "Returns the health status of the API")
    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        response.put("service", "RepatRoom Backend API");
        response.put("version", "1.0.0");
        return response;
    }
}