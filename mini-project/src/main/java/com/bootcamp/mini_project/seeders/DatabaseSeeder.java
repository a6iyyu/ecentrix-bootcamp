package com.bootcamp.mini_project.seeders;

import com.bootcamp.mini_project.entity.User;
import com.bootcamp.mini_project.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the initial system user account on application startup if empty.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seeder.username:admin}")
    private String adminUsername;

    @Value("${app.seeder.password:admin123}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String @NonNull ... args) {
        if (userRepository.count() > 0) {
            log.info("[SEEDER] Users table is already populated. Skipping initialization.");
            return;
        }

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setEmail("admin@bootcamp.com");
        admin.setPassword(passwordEncoder.encode(adminPassword));

        userRepository.save(admin);
        log.info("[SEEDER] Default admin user successfully created!");
    }
}