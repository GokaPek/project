package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                    "admin",
                    "admin@course-manager.com",
                    passwordEncoder.encode("admin123"),
                    Set.of("ADMIN", "USER")
            );
            userRepository.save(admin);
            log.info("✅ Admin created: admin/admin123");
        }

        if (!userRepository.existsByUsername("user")) {
            User demoUser = new User(
                    "user",
                    "user@example.com",
                    passwordEncoder.encode("user123"),
                    Set.of("USER")
            );
            userRepository.save(demoUser);
            log.info("✅ User created: user/user123");
        }
    }
}
