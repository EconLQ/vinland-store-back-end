package com.vinland.store.security.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {
    @NotNull
    @NotBlank
    @Size(min = 2, max = 30)
    private String username;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 30)
    private String firstName;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 30)
    private String lastName;

    @NotNull
    @NotBlank
    @Email
    private String email;

    @NotNull
    @NotBlank
    private String password;
}
