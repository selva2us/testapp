package com.supermarket.pos_backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Shop management APIs")
public class HealthController {

    @GetMapping("/api/health")
    public String healthCheck() {
        return "Supermarket POS Backend is running ✅";
    }
}
