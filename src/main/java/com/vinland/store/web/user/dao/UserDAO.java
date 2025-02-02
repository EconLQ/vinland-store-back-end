package com.vinland.store.web.user.dao;

import com.vinland.store.web.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    @Query(value = "SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);
}
