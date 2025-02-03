package com.vinland.store.security.auth.controller;

import com.vinland.store.security.auth.model.LoginRequest;
import com.vinland.store.security.auth.model.LoginResponse;
import com.vinland.store.security.auth.model.RegistrationRequest;
import com.vinland.store.security.auth.service.AuthenticationService;
import com.vinland.store.utils.MessageResponse;
import com.vinland.store.utils.PathConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(PathConstants.AUTH)
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public LoginResponse signInUser(@ModelAttribute LoginRequest loginRequest) {
        return authenticationService.signIn(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public MessageResponse signUpUser(@ModelAttribute RegistrationRequest request) {
        authenticationService.signUp(request);
        return new MessageResponse("User registered successfully");
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestHeader(value = "Authorization") String authorizationHeader) {
        String refreshToken = authorizationHeader.substring("Bearer ".length());
        LoginResponse response = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}
