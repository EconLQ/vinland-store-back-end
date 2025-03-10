package com.vinland.store.security.auth.service;

import com.vinland.store.security.auth.model.LoginResponse;
import com.vinland.store.security.auth.model.RegistrationRequest;
import com.vinland.store.utils.exception.JwtTokenException;
import com.vinland.store.utils.exception.UserAlreadyExistException;
import com.vinland.store.web.user.model.Role;
import com.vinland.store.web.user.model.User;
import com.vinland.store.web.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public Map<String, String> signIn(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                email,
                password
        ));
        User user = (User) userService.loadUserByUsername(email);
        return generateCleanTokens(user);
    }

    public User signUp(RegistrationRequest request) throws UserAlreadyExistException {
        if (Boolean.TRUE.equals(userService.existsByEmail(request.getEmail()))) {
            throw new UserAlreadyExistException("User with email " + request.getEmail() + " already exists");
        }
        return userService.create(User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .isEmailVerified(false)
                .roles(Set.of(Role.USER))
                .build());
    }

    private Map<String, String> generateCleanTokens(User user) {
        final String accessToken = jwtService.generateAccessToken(user).substring("Bearer ".length());
        final String refreshToken = jwtService.generateRefreshToken(user).substring("Bearer ".length());
        return Map.of(
                ACCESS_TOKEN, accessToken,
                REFRESH_TOKEN, refreshToken
        );
    }

    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new JwtTokenException("Only refresh tokens allowed");
        }

        String email = jwtService.extractEmail(refreshToken);
        User user = (User) userService.loadUserByUsername(email);
        Map<String, String> tokens = generateCleanTokens(user);
        return new LoginResponse(tokens.get(ACCESS_TOKEN));
    }
}
