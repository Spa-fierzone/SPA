package com.spazone.service.impl;

import com.spazone.dto.*;
import com.spazone.entity.Role;
import com.spazone.entity.User;
import com.spazone.enums.Gender;
import com.spazone.exception.ResourceNotFoundException;
import com.spazone.exception.InvalidTokenException;
import com.spazone.exception.UserAlreadyExistsException;
import com.spazone.repository.RoleRepository;
import com.spazone.repository.UserRepository;
import com.spazone.service.CloudinaryService;
import com.spazone.service.EmailService;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Value("${app.upload.dir:${user.home}/spa-zone/uploads}")
    private String uploadDir;

    @Override
    public void registerUser(RegisterDto registerDto) {
        // Check if user already exists
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setFullName(registerDto.getFullName());
        user.setPhone(registerDto.getPhone());
        user.setDateOfBirth(registerDto.getDateOfBirth());
        user.setGender(Gender.valueOf(registerDto.getGender().toUpperCase()));
        user.setAddress(registerDto.getAddress());
        user.setEnabled(false);
        user.setEmailVerified(false);
        user.setStatus("active");

        // Generate verification token
        String otpCode = String.format("%06d", (int)(Math.random() * 1_000_000));
        user.setOtpCode(otpCode);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

        // Assign default role
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseGet(() -> roleRepository.save(new Role("CUSTOMER")));
        user.setRoles(Set.of(customerRole));

        userRepository.save(user);

        try {
            emailService.sendVerificationEmail(user.getEmail(), otpCode);
        } catch (Exception ex) {
            System.err.println("Failed to send OTP email: " + ex.getMessage());
        }
    }

    @Override
    public boolean verifyEmail(String email, String otpCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getOtpCode() == null || !user.getOtpCode().equals(otpCode)) {
            throw new InvalidTokenException("Invalid OTP code");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("OTP code has expired");
        }

        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);

        return true;
    }

    @Override
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        user.setLastPasswordChange(LocalDateTime.now());
        userRepository.save(user);

        return true;
    }

    @Override
    public void changePassword(String usernameOrEmail, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));

        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        user.setLastPasswordChange(LocalDateTime.now());
        userRepository.save(user);

        // Send confirmation email
        emailService.sendPasswordChangeConfirmation(user.getEmail());
    }

    @Override
    public UserProfileDto getUserProfile(String usernameOrEmail) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));

        return mapToUserProfileDto(user);
    }


    @Override
    public void updateUserProfile(String usernameOrEmail, UpdateProfileDto updateProfileDto) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));

        user.setFullName(updateProfileDto.getFullName());
        user.setPhone(updateProfileDto.getPhone());
        user.setDateOfBirth(updateProfileDto.getDateOfBirth());
        user.setGender(Gender.valueOf(updateProfileDto.getGender().toUpperCase()));
        user.setAddress(updateProfileDto.getAddress());

        userRepository.save(user);
    }

    @Override
    public void updateProfilePicture(String usernameOrEmail, MultipartFile file) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));

        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            user.setProfilePicture(imageUrl);
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload profile picture to Cloudinary", e);
        }
    }


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public Page<User> getUsersWithFilters(String keyword, String status, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.searchUsers(keyword, status, role, pageable);
    }

    @Override
    public void changeUserStatus(int userId, String status) {
        userRepository.updateStatusById(userId, status);
    }

    @Override
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public void updateUserRoles(Integer userId, Set<Integer> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Set<Role> newRoles = new HashSet<>(roleRepository.findAllById(roleIds));
        user.setRoles(newRoles);
        userRepository.save(user);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findByRoleNameNot("ADMIN");
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void createUserWithRoleAndSalary(CreateUserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAddress(dto.getAddress());
        user.setEnabled(true);
        user.setStatus("active");
        user.setEmailVerified(true);

        String roleName = dto.getRole().toUpperCase();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
        user.setRoles(Set.of(role));

        BigDecimal salary = dto.getBaseSalary();
        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            Map<String, BigDecimal> defaultSalaries = Map.of(
                    "MANAGER", new BigDecimal("15000000"),
                    "TECHNICIAN", new BigDecimal("10000000"),
                    "RECEPTIONIST", new BigDecimal("8000000")
            );
            salary = defaultSalaries.getOrDefault(roleName, BigDecimal.ZERO);
        }
        user.setBaseSalary(salary);

        userRepository.save(user);
    }

    @Override
    public List<User> findTechniciansByBranch(Integer branchId) {
        return userRepository.findByBranchIdAndRoleName(branchId, "TECHNICIAN");
    }

    @Override
    public List<User> findTop3Technicians() {
        return userRepository.findTop3Technicians(PageRequest.of(0, 3));
    }

    private String saveProfilePicture(MultipartFile file, String username) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = username + "_" + System.currentTimeMillis() + extension;

        Path uploadPath = Paths.get(uploadDir + "/profiles");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/profiles/" + newFilename;
    }

    private UserProfileDto mapToUserProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setGender(user.getGender() != null ? user.getGender().name() : null);
        dto.setAddress(user.getAddress());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setVipLevel(user.getVipLevel());
        dto.setEmailVerified(user.getEmailVerified());
        dto.setPhoneVerified(user.getPhoneVerified());
        dto.setTwoFactorEnabled(user.getTwoFactorEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        return dto;
    }
}