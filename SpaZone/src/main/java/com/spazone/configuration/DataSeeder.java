package com.spazone.configuration;

import com.spazone.entity.*;
import com.spazone.enums.Gender;
import com.spazone.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

@Configuration
public class DataSeeder {

    @Autowired
    private ServiceScheduleRepository serviceScheduleRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepo;

    @Bean
    public CommandLineRunner seedUsers(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed MANAGER role if it doesn't exist
            Role managerRole = roleRepo.findByRoleName("MANAGER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setRoleName("MANAGER");
                        newRole.setDescription("Branch Manager Role");
                        return roleRepo.save(newRole);
                    });

            // Check if manager users already exist before adding
            for (int i = 1; i <= 5; i++) {
                String username = "manager" + i;
                if (!userRepo.existsByUsername(username)) {
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

            // Seed TECHNICIAN role if it doesn't exist
            Role technicianRole = roleRepo.findByRoleName("TECHNICIAN")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setRoleName("TECHNICIAN");
                        newRole.setDescription("Technician Role");
                        return roleRepo.save(newRole);
                    });

            // Check if technician users already exist before adding
            for (int i = 1; i <= 5; i++) {
                String username = "tech" + i;
                if (!userRepo.existsByUsername(username)) {
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

            // Seed ServiceSchedule data if not already added
            User technician1 = userRepo.findByUsername("tech1").orElse(null);
            User technician2 = userRepo.findByUsername("tech2").orElse(null);
            User technician3 = userRepo.findByUsername("tech3").orElse(null);
            Branch branch1 = branchRepository.findById(1).orElse(null);
            Branch branch2 = branchRepository.findById(2).orElse(null);
            Service service1 = serviceRepository.findById(1).orElse(null);
            Service service2 = serviceRepository.findById(2).orElse(null);

            if (technician1 != null && technician2 != null && technician3 != null && branch1 != null && branch2 != null && service1 != null && service2 != null) {
                for (int i = 0; i < 10; i++) {
                    // Check if the schedule already exists
                    if (!serviceScheduleRepository.existsByTechnicianAndServiceAndBranch(technician1, service1, branch1)) {
                        ServiceSchedule schedule = new ServiceSchedule();
                        schedule.setTechnician(technician1);
                        schedule.setBranch(i % 2 == 0 ? branch1 : branch2);
                        schedule.setService(i % 2 == 0 ? service1 : service2);
                        schedule.setStartTime(LocalTime.of(8 + (i % 3), 0));
                        schedule.setEndTime(LocalTime.of(16 + (i % 3), 0));
                        schedule.setDayOfWeek(i % 7 + 1);
                        schedule.setActive(true);
                        serviceScheduleRepository.save(schedule);
                    }
                }

                // Adding more schedules for technician2 and technician3
                for (int i = 0; i < 5; i++) {
                    if (!serviceScheduleRepository.existsByTechnicianAndServiceAndBranch(i % 2 == 0 ? technician2 : technician3, service1, branch1)) {
                        ServiceSchedule schedule = new ServiceSchedule();
                        schedule.setTechnician(i % 2 == 0 ? technician2 : technician3);
                        schedule.setBranch(i % 2 == 0 ? branch1 : branch2);
                        schedule.setService(i % 2 == 0 ? service1 : service2);
                        schedule.setStartTime(LocalTime.of(8 + (i % 3), 0));
                        schedule.setEndTime(LocalTime.of(16 + (i % 3), 0));
                        schedule.setDayOfWeek(i % 7 + 1);
                        schedule.setActive(true);
                        serviceScheduleRepository.save(schedule);
                    }
                }
            }
        };
    }
}


