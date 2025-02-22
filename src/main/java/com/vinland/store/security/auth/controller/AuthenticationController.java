package com.vinland.store.security.auth.controller;

import com.vinland.store.security.auth.model.LoginRequest;
import com.vinland.store.security.auth.model.LoginResponse;
import com.vinland.store.security.auth.model.RegistrationRequest;
import com.vinland.store.security.auth.service.AuthenticationService;
import com.vinland.store.security.auth.service.JwtService;
import com.vinland.store.utils.MessageResponse;
import com.vinland.store.utils.PathConstants;
import com.vinland.store.utils.exception.UserAlreadyExistException;
import com.vinland.store.web.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(PathConstants.AUTH)
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<LoginResponse> signInUser(@ModelAttribute LoginRequest loginRequest, HttpServletResponse response) {
        final Map<String, String> tokens = authenticationService.signIn(loginRequest.getEmail(), loginRequest.getPassword());
        final Cookie cookie = jwtService.createCookieWithJwt(tokens.get("refreshToken"), "refreshToken");
        response.addCookie(cookie);
        return ResponseEntity.ok().body(new LoginResponse(tokens.get("accessToken")));
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
    public ResponseEntity<MessageResponse> logout(HttpServletResponse response) {
        final Cookie cookie = jwtService.removeCookieWithJwt("refreshToken");
        response.addCookie(cookie);
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = null;
        // get refresh token from cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Refresh token not found"));
        }
        if (!jwtService.isRefreshToken(refreshToken)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid refresh token"));
        }
        String username = jwtService.extractEmail(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(username);
        if (jwtService.isTokenValid(refreshToken, userDetails)) {
            String newAccessToken = jwtService.generateAccessToken(userDetails).substring("Bearer ".length());
            return ResponseEntity.ok(new LoginResponse(newAccessToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Refresh token expired"));
        }
    }
}
