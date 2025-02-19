package com.vinland.store.web.user.service;

import com.vinland.store.web.user.dao.UserDAO;
import com.vinland.store.web.user.model.User;
import com.vinland.store.web.user.model.UserDTO;
import com.vinland.store.web.user.model.UserInfoDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final PasswordEncoder passwordEncoder;
    private final UserDAO userDAO;
    private final UserMapperService userMapper;

    private static void updateUserFields(User user, UserDTO userDTO) {
        Map<Consumer<String>, String> updates = new HashMap<>();
        updates.put(user::setUsername, userDTO.getUsername());
        updates.put(user::setFirstName, userDTO.getFirstName());
        updates.put(user::setLastName, userDTO.getLastName());
        updates.put(user::setEmail, userDTO.getEmail());
        for (Consumer<String> consumer : updates.keySet()) {
            if (updates.get(consumer) != null || !updates.get(consumer).isEmpty()) {
                consumer.accept(updates.get(consumer));
            }
        }
    }

    public Optional<UserInfoDTO> getCurrentUser(User user) {
        return Optional.ofNullable(userMapper.entityToUserInfoDTO(user));
    }

    @Transactional
    public UserInfoDTO updateUser(User user, @Valid UserDTO userDTO) {
        if (userDTO.getNewPassword() != null && !userDTO.getNewPassword().isEmpty()) {
            updatePassword(user, userDTO);
        }
        updateUserFields(user, userDTO);
        return userMapper.entityToUserInfoDTO(userDAO.save(user));
    }

    private void updatePassword(User user, @Valid UserDTO userDTO) {
        if (!passwordEncoder.matches(userDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Wrong old password");
        } else {
            user.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));
        }
    }
}
