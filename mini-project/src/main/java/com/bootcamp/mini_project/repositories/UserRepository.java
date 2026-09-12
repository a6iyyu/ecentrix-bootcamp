package com.bootcamp.mini_project.repositories;

import com.bootcamp.mini_project.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides database operation methods for {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Checks whether an email address is already registered.
     *
     * @param email the email address to check
     * @return {@code true} if the email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks whether a username is already taken.
     *
     * @param username the username to check
     * @return {@code true} if the username exists
     */
    boolean existsByUsername(String username);

    /**
     * Finds a user record by email address for authentication.
     *
     * @param email the target email address
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user record by username for authentication.
     *
     * @param username the target username
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByUsername(String username);
}