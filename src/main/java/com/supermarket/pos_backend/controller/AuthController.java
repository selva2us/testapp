package com.supermarket.pos_backend.controller;

import com.supermarket.pos_backend.dto.LoginRequest;
import com.supermarket.pos_backend.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


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

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> loginData = authService.login(request.getEmailOrMobile(), request.getPassword());

        String token = (String) loginData.get("token");
        String role = (String) loginData.get("role");
        String email = (String) loginData.get("email");

        return ResponseEntity.ok(new JwtResponse(token, role, email));
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


    // Inner class for response
    static class JwtResponse {
        private String token;
        private String role;
        private String email;

        public JwtResponse(String token, String role, String email) {
            this.token = token;
            this.role = role;
            this.email = email;
        }
        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public JwtResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
