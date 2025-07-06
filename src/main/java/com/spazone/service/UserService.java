
// UserService.java
package com.spazone.service;

import com.spazone.dto.*;
import com.spazone.entity.Role;
import com.spazone.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface UserService {

    void registerUser(RegisterDto registerDto);

    boolean verifyEmail(String emai, String otpCode);

    void initiatePasswordReset(String email);

    boolean resetPassword(String token, String newPassword);

    void changePassword(String username, ChangePasswordDto changePasswordDto);

    UserProfileDto getUserProfile(String username);

    void updateUserProfile(String username, UpdateProfileDto updateProfileDto);

    void updateProfilePicture(String username, MultipartFile file);

    User findByUsername(String username);

    User findByEmail(String email);

    Page<User> getUsersWithFilters(String keyword, String status, String role, int page, int size);

    void changeUserStatus(int userId, String status);

    User getUserById(int userId);

    void updateUserRoles(Integer userId, Set<Integer> roleIds);

    List<Role> getAllRoles();

    void saveUser(User user);

    void createUserWithRoleAndSalary(CreateUserDto createUserDto);

    List<User> findTechniciansByBranch(Integer branchId);

    List<User> findTop3Technicians();
}