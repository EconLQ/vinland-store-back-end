package com.vinland.store.security.auth.controller;

import com.vinland.store.security.auth.model.LoginRequest;
import com.vinland.store.security.auth.model.LoginResponse;
import com.vinland.store.security.auth.model.RegistrationRequest;
import com.vinland.store.security.auth.service.AuthenticationService;
import com.vinland.store.utils.MessageResponse;
import com.vinland.store.utils.PathConstants;
import com.vinland.store.utils.exception.UserAlreadyExistException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(PathConstants.AUTH)
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<MessageResponse> signInUser(@ModelAttribute LoginRequest loginRequest) {
        final LoginResponse response = authenticationService.signIn(loginRequest.getEmail(), loginRequest.getPassword());
        final String cleanedAccessToken = response.getAccessToken().substring("Bearer ".length());
        ResponseCookie cookie = ResponseCookie.from("accessToken", cleanedAccessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new MessageResponse("User logged in successfully"));
    }

    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> signUpUser(@Valid @ModelAttribute RegistrationRequest request) {
        try {
            authenticationService.signUp(request);
            return ResponseEntity.ok(new MessageResponse("User registered successfully"));
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestHeader(value = "Cookie") String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank() || !cookieHeader.contains("accessToken")) {
            return ResponseEntity.ok().body(new MessageResponse("User is not logged in"));
        }
        ResponseCookie cookie = ResponseCookie.from("accessToken", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new MessageResponse("Logged out successfully"));
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestHeader(value = "Authorization") String authorizationHeader) {
        String refreshToken = authorizationHeader.substring("Bearer ".length());
        LoginResponse response = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}
