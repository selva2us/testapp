package com.supermarket.pos_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Email or Mobile is required")
    private String emailOrMobile;

    @NotBlank(message = "Password is required")
    private String password;
}
