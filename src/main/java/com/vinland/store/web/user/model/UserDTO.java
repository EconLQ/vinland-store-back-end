package com.vinland.store.web.user.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @Size(max = 100)
    private String username;
    @Size(max = 25)
    private String firstName;
    @Size(max = 25)
    private String lastName;
    @Size(max = 125)
    private String email;
    @Size(max = 1000)
    private String oldPassword;
    @Size(max = 1000)
    private String newPassword;
}
