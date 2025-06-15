package com.spazone.configuration;

import com.spazone.entity.Role;
import com.spazone.entity.User;
import com.spazone.enums.Gender;
import com.spazone.repository.RoleRepository;
import com.spazone.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedUsers(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed MANAGER role
            Role managerRole = roleRepo.findByRoleName("MANAGER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setRoleName("MANAGER");
                        newRole.setDescription("Branch Manager Role");
                        return roleRepo.save(newRole);
                    });

            for (int i = 1; i <= 5; i++) {
                String username = "manager" + i;
                if (userRepo.findByUsername(username).isEmpty()) {
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(passwordEncoder.encode("password"));
                    user.setEmail(username + "@spa.com");
                    user.setFullName("Manager " + i);
                    user.setPhone("090100000" + i);
                    user.setGender(Gender.MALE);
                    user.setDateOfBirth(LocalDate.of(1990, 1, i));
                    user.setAddress("Địa chỉ số " + i);
                    user.setStatus("active");
                    user.setEnabled(true);
                    user.setRoles(Collections.singleton(managerRole));
                    userRepo.save(user);
                }
            }

            // Seed TECHNICIAN role
            Role technicianRole = roleRepo.findByRoleName("TECHNICIAN")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setRoleName("TECHNICIAN");
                        newRole.setDescription("Technician Role");
                        return roleRepo.save(newRole);
                    });

            for (int i = 1; i <= 5; i++) {
                String username = "tech" + i;
                if (userRepo.findByUsername(username).isEmpty()) {
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(passwordEncoder.encode("password"));
                    user.setEmail(username + "@spa.com");
                    user.setFullName("Technician " + i);
                    user.setPhone("090200000" + i);
                    user.setGender(Gender.FEMALE);
                    user.setDateOfBirth(LocalDate.of(1992, 2, i));
                    user.setAddress("Kỹ thuật viên số " + i);
                    user.setStatus("active");
                    user.setEnabled(true);
                    user.setRoles(Collections.singleton(technicianRole));
                    userRepo.save(user);
                }
            }
        };
    }
}
