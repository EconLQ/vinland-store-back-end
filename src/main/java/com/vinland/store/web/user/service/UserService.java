package com.vinland.store.web.user.service;

import com.vinland.store.utils.exception.EmailAlreadyExistsException;
import com.vinland.store.web.user.dao.UserDAO;
import com.vinland.store.web.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserDAO userDAO;

    public Optional<User> loadUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + username + " not found"));
    }

    public User create(User user) {
        if (Boolean.TRUE.equals(userDAO.existsByEmail(user.getEmail()))) {
            throw new EmailAlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }
        return userDAO.save(user);
    }
}
