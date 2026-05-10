package com.parkzen.config;

import com.parkzen.entity.Admin;
import com.parkzen.enums.Role;
import com.parkzen.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AppConfig {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedAdmin() {
        return args -> {
            if (!adminRepository.existsByEmail("admin@parkzen.com")) {
                Admin admin = Admin.builder()
                        .email("admin@parkzen.com")
                        .password(passwordEncoder.encode("admin@123"))
                        .role(Role.ROLE_ADMIN)
                        .build();
                adminRepository.save(admin);
                log.info("Default admin seeded: admin@parkzen.com / admin@123");
            }
        };
    }
}
