package com.vinland.store.web.user.service;

import com.vinland.store.utils.exception.EmailAlreadyExistsException;
import com.vinland.store.web.user.dao.UserDAO;
import com.vinland.store.web.user.model.User;
import jakarta.transaction.Transactional;
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
        String emailPattern = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$";
        if (!username.matches(emailPattern)) {
            return userDAO.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
        } else {
            return loadUserByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User with email " + username + " not found"));
        }
    }

    @Transactional
    public User create(User user) {
        if (Boolean.TRUE.equals(existsByEmail(user.getEmail()))) {
            throw new EmailAlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }
        return userDAO.save(user);
    }

    public Boolean existsByEmail(String email) {
        return userDAO.existsByEmail(email);
    }
}
