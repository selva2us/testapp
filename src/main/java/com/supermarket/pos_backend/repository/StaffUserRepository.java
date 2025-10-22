package com.supermarket.pos_backend.repository;

import com.supermarket.pos_backend.model.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StaffUserRepository extends JpaRepository<StaffUser, Long> {
    List<StaffUser> findByAdminId(Long adminId);
    Optional<StaffUser> findByEmail(String email);
    Optional<StaffUser> findByMobile(String mobile);
}

