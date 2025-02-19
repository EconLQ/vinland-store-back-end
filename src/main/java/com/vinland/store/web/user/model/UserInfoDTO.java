package com.vinland.store.web.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class UserInfoDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;
}
