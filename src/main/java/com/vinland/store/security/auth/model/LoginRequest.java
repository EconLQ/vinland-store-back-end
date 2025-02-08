package com.vinland.store.security.auth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    @Size(min = 2, max = 30, message = "Username must be between 2 and 30 characters long")
    @NotNull
    private String email;

    @NotNull
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @NotBlank
    private String password;
}
