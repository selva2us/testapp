package com.supermarket.pos_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Entity
@Table(name = "staff_users")
@Getter
@Setter
public class StaffUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Column(unique = true)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 100, message = "Password must be between 4 and 50 characters")
    private String password;

    @Column(unique = true)
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid mobile number format")
    private String mobile; // ✅ Added mobile number

    @NotBlank(message = "Role is required")
    private String role = "Cashier";

    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference
    private AdminUser admin;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    // Getters and setters
}

