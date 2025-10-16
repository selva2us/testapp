package com.supermarket.pos_backend.controller;

import com.supermarket.pos_backend.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Shop management APIs")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String mobileNumber,
                                          @RequestParam String deviceId) {
        authService.sendOtp(mobileNumber, deviceId);
        return ResponseEntity.ok("OTP sent (check logs)");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String mobileNumber,
                                            @RequestParam String deviceId,
                                            @RequestParam String otp,
                                            @RequestParam String role) {
        String token = authService.verifyOtp(mobileNumber, deviceId, otp, role);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String mobileNumber,
                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        authService.logout(mobileNumber, token);
        return ResponseEntity.ok("Logged out successfully");
    }
}
