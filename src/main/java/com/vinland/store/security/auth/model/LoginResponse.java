package com.vinland.store.security.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    @JsonProperty(value = "access_token", required = true)
    private String accessToken;

    @JsonProperty(value = "refresh_token", required = true)
    private String refreshToken;

}
