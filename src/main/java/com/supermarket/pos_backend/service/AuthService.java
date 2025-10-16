package com.supermarket.pos_backend.service;

import com.supermarket.pos_backend.model.OtpCode;
import com.supermarket.pos_backend.model.User;
import com.supermarket.pos_backend.repository.OtpRepository;
import com.supermarket.pos_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpRepository otpRepo;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    // Generate OTP
    public String sendOtp(String mobileNumber, String deviceId) {
        String otp = String.valueOf((int)(Math.random() * 9000) + 1000); // 4-digit OTP
        OtpCode otpCode = new OtpCode();
        otpCode.setMobileNumber(mobileNumber);
        otpCode.setDeviceId(deviceId);
        otpCode.setOtpCode(otp);
        otpCode.setExpiresAt(Instant.now().plusSeconds(300)); // 5 min expiry
        otpCode.setVerified(false);
        otpRepo.save(otpCode);
        System.out.println("OTP for " + mobileNumber + ": " + otp);
        return otp;
    }

    // Verify OTP + device + role
    public String verifyOtp(String mobileNumber, String deviceId, String otp, String role) {
        OtpCode otpCode = otpRepo.findByMobileNumberAndDeviceIdAndVerifiedFalse(mobileNumber, deviceId)
                .orElseThrow(() -> new RuntimeException("OTP not found or expired"));

        if (!otpCode.getOtpCode().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        otpCode.setVerified(true);
        otpRepo.save(otpCode);

        // Check user
        User user = userRepo.findByMobileNumber(mobileNumber)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setMobileNumber(mobileNumber);
                    newUser.setDeviceId(deviceId);
                    newUser.setRoles(new HashSet<>(Set.of(role)));
                    return userRepo.save(newUser);
                });

        if (user.getDeviceId() != null && !user.getDeviceId().equals(deviceId)) {
            throw new RuntimeException("This mobile number is already logged in from another device");
        }

        user.setDeviceId(deviceId);
        user.setActiveSession(true);
        user.setRoles(new HashSet<>(Set.of(role)));
        userRepo.save(user);

        // Generate JWT
        return jwtService.createToken(user.getId(), role);
    }

    public void logout(String mobileNumber, String token) {
        User user = userRepo.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Deactivate the session
        user.setActiveSession(false);
        userRepo.save(user);

        // Optional: blacklist the token so it can't be used again
        if (token != null && !token.isEmpty()) {
            tokenBlacklistService.blacklistToken(token);
        }
    }
}