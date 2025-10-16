package com.supermarket.pos_backend.repository;

import com.supermarket.pos_backend.model.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByMobileNumberAndDeviceIdAndVerifiedFalse(String mobileNumber, String deviceId);
}
