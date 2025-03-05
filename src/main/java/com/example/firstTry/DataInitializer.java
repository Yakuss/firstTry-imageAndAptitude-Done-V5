package com.example.firstTry;

import com.example.firstTry.model.Admin;
import com.example.firstTry.Enums.Role;
import com.example.firstTry.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final AdminRepository adminRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (adminRepository.findByEmail("admin@hospital.com").isEmpty()) {
            Admin admin = new Admin();
            admin.setEmail("admin@hospital.com");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setFirstName("ali");
            admin.setLastName("yassin");
            adminRepository.save(admin);
        }
    }
}
