package com.vinland.store.web.user.service;

import com.vinland.store.security.auth.service.JwtService;
import com.vinland.store.web.user.dao.UserDAO;
import com.vinland.store.web.user.model.UpdateUserResult;
import com.vinland.store.web.user.model.User;
import com.vinland.store.web.user.model.UserDTO;
import com.vinland.store.web.user.model.UserInfoDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final PasswordEncoder passwordEncoder;
    private final UserDAO userDAO;
    private final UserMapperService userMapper;
    private final JwtService jwtService;
    private final UserService userService;

    private static void updateUserFields(User user, UserDTO userDTO) {
        Map<Consumer<String>, String> updates = Map.of(
                user::setUsername, userDTO.getUsername(),
                user::setFirstName, userDTO.getFirstName(),
                user::setLastName, userDTO.getLastName(),
                user::setEmail, userDTO.getEmail()
        );

        updates.forEach((setter, value) -> Optional.ofNullable(value).ifPresent(setter));
    }

    public Optional<UserInfoDTO> getCurrentUser(User user) {
        return Optional.ofNullable(userMapper.entityToUserInfoDTO(user));
    }

    @Transactional
    public UpdateUserResult updateUser(User user, @Valid UserDTO userDTO) {
        if (userDTO.getNewPassword() != null && !userDTO.getNewPassword().isEmpty()) {
            updatePassword(user, userDTO);
        }

        updateUserFields(user, userDTO);
        User updatedUser = userDAO.save(user);

        // update authentication to reflect new username/email
        updateSecurityContext(updatedUser);

        UserInfoDTO updatedUserInfo = userMapper.entityToUserInfoDTO(updatedUser);
        String newAccessToken = null;
        // check if email or username changed
        boolean emailChanged = !user.getEmail().equals(userDTO.getEmail());
        boolean usernameChanged = !user.getUsername().equals(userDTO.getUsername());
        if (emailChanged || usernameChanged) {
            newAccessToken = generateNewAccessToken(updatedUser);
        }

        return new UpdateUserResult(updatedUserInfo, newAccessToken, emailChanged || usernameChanged);
    }

    private void updateSecurityContext(User updatedUser) {
        UserDetails newUserDetails = userService.loadUserByUsername(updatedUser.getUsername());

        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(
                        newUserDetails,
                        newUserDetails.getPassword(),
                        newUserDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    private void updatePassword(User user, @Valid UserDTO userDTO) {
        if (!passwordEncoder.matches(userDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Wrong old password");
        }
        user.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));
    }

    private String generateNewAccessToken(User user) {
        return jwtService.generateAccessToken(user).substring("Bearer ".length());
    }
}
