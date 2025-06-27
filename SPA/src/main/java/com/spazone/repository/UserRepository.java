package com.spazone.repository;

import com.spazone.entity.Role;
import com.spazone.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("""
    SELECT DISTINCT u FROM User u 
    LEFT JOIN u.roles r 
    WHERE (:keyword IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) 
                        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:status IS NULL OR u.status = :status)
      AND (:role IS NULL OR r.roleName = :role)
      AND NOT EXISTS (
          SELECT 1 FROM u.roles r2 WHERE r2.roleName = 'ADMIN'
      )
""")
    Page<User> searchUsers(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("role") String role,
            Pageable pageable);


    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.userId = :id")
    void updateStatusById(@Param("id") Integer id, @Param("status") String status);

    @Query("SELECT u FROM User u LEFT JOIN u.roles r WHERE u.status = 'active' AND u.enabled = true AND r.roleName <> 'ADMIN'")
    List<User> findActiveUsers();

    @Query("""
    SELECT DISTINCT u FROM User u 
    JOIN u.roles r 
    WHERE r.roleName IN ('RECEPTIONIST', 'TECHNICIAN', 'MANAGER')
""")
    List<User> findUsersByMainRoles();

    @Query("""
    SELECT u FROM User u 
    JOIN u.roles r 
    WHERE r.roleName = 'TECHNICIAN' 
""")
    List<User> findTop3Technicians(Pageable pageable);


    @Query("SELECT u FROM User u WHERE u.status = 'active' AND u.enabled = true AND u.preferredBranchId = :branchId")
    List<User> findActiveUsersByBranch(@Param("branchId") Integer branchId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    Optional<User> findByResetPasswordToken(String token);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.loginAttempts = u.loginAttempts + 1, u.lastLoginAttempt = :lastAttempt WHERE u.username = :username")
    void incrementLoginAttempts(String username, LocalDateTime lastAttempt);

    @Modifying
    @Query("UPDATE User u SET u.loginAttempts = 0 WHERE u.username = :username")
    void resetLoginAttempts(String username);

    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.username = :username")
    void updateLastLogin(String username, LocalDateTime lastLogin);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName")
    List<User> findUsersByRole(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.branch.branchId = :branchId AND r.roleName = :roleName")
    List<User> findByBranchIdAndRoleName(@Param("branchId") Integer branchId, @Param("roleName") String roleName);

}
