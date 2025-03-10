package com.vinland.store.web.user.controller;

import com.vinland.store.utils.PathConstants;
import com.vinland.store.utils.exception.UserNotFoundException;
import com.vinland.store.web.user.model.UpdateUserResult;
import com.vinland.store.web.user.model.User;
import com.vinland.store.web.user.model.UserDTO;
import com.vinland.store.web.user.model.UserInfoDTO;
import com.vinland.store.web.user.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@CrossOrigin
@RequestMapping(PathConstants.ACCOUNT)
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping(value = "/me", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfoDTO> getCurrentUser(@AuthenticationPrincipal User user) {
        final UserInfoDTO currentUser = accountService.getCurrentUser(user).orElseThrow(() -> new UserNotFoundException("User not found"));
        return ResponseEntity.ok(currentUser);
    }

    @PutMapping(consumes = APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<UserInfoDTO> updateCurrentUser(@AuthenticationPrincipal User user, @Valid @ModelAttribute UserDTO userDTO) {
        UpdateUserResult updateResult = accountService.updateUser(user, userDTO);

        if (updateResult.emailOrUsernameChanged()) {
            ResponseCookie cookie = ResponseCookie.from("accessToken", updateResult.newAccessToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("None")
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(updateResult.updatedUser());
        }

        return ResponseEntity.ok(updateResult.updatedUser());
    }
}
