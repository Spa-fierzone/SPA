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

    // Basic find methods
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetPasswordToken(String token);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Search users with filters
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

    // Update user status
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.userId = :id")
    void updateStatusById(@Param("id") Integer id, @Param("status") String status);

    // Find active users (excluding admins)
    @Query("SELECT u FROM User u LEFT JOIN u.roles r WHERE u.status = 'active' AND u.enabled = true AND r.roleName <> 'ADMIN'")
    List<User> findActiveUsers();

    // Find users by role names - THIS IS THE MISSING METHOD
    @Query("""
    SELECT DISTINCT u FROM User u 
    JOIN u.roles r 
    WHERE r.roleName IN :roleNames
    AND u.status = 'active' 
    AND u.enabled = true
    ORDER BY u.fullName
    """)
    List<User> findByRoleNames(@Param("roleNames") List<String> roleNames);

    // Find users by main roles (staff only)
    @Query("""
    SELECT DISTINCT u FROM User u 
    JOIN u.roles r 
    WHERE r.roleName IN ('RECEPTIONIST', 'TECHNICIAN', 'MANAGER')
    AND u.status = 'active'
    AND u.enabled = true
    """)
    List<User> findUsersByMainRoles();

    // Find technicians by branch
    @Query("""
    SELECT DISTINCT u FROM User u 
    JOIN u.roles r 
    WHERE r.roleName = :roleName 
    AND u.preferredBranchId = :branchId
    AND u.status = 'active'
    AND u.enabled = true
    """)
    List<User> findByBranchIdAndRoleName(@Param("branchId") Integer branchId, @Param("roleName") String roleName);

    // Find top 3 technicians (for dashboard or reports)
    @Query("""
    SELECT u FROM User u 
    JOIN u.roles r 
    WHERE r.roleName = 'TECHNICIAN'
    AND u.status = 'active'
    AND u.enabled = true
    ORDER BY u.performanceScore DESC, u.totalWorkingHours DESC
    """)
    List<User> findTop3Technicians(Pageable pageable);

    // Find users by VIP level
    @Query("SELECT u FROM User u WHERE u.vipLevel = :vipLevel AND u.status = 'active'")
    List<User> findByVipLevel(@Param("vipLevel") String vipLevel);

    // Count users by role
    @Query("""
    SELECT COUNT(DISTINCT u) FROM User u 
    JOIN u.roles r 
    WHERE r.roleName = :roleName 
    AND u.status = 'active'
    """)
    Long countByRoleName(@Param("roleName") String roleName);

    // Find users by multiple criteria
    @Query("""
    SELECT DISTINCT u FROM User u 
    LEFT JOIN u.roles r 
    WHERE (:status IS NULL OR u.status = :status)
    AND (:enabled IS NULL OR u.enabled = :enabled)
    AND (:roleName IS NULL OR r.roleName = :roleName)
    ORDER BY u.createdAt DESC
    """)
    List<User> findByCriteria(
        @Param("status") String status,
        @Param("enabled") Boolean enabled,
        @Param("roleName") String roleName
    );

    // Find active users by branch - MISSING METHOD FOR SALARY SERVICE
    @Query("""
    SELECT DISTINCT u FROM User u 
    JOIN u.roles r 
    WHERE u.preferredBranchId = :branchId
    AND u.status = 'active' 
    AND u.enabled = true
    AND r.roleName IN ('TECHNICIAN', 'RECEPTIONIST', 'MANAGER')
    ORDER BY u.fullName
    """)
    List<User> findActiveUsersByBranch(@Param("branchId") Integer branchId);

    // Find users by specific role - MISSING METHOD FOR BRANCH ADMIN CONTROLLER
    @Query("""
    SELECT DISTINCT u FROM User u 
    JOIN u.roles r 
    WHERE r.roleName = :roleName
    AND u.status = 'active' 
    AND u.enabled = true
    ORDER BY u.fullName
    """)
    List<User> findUsersByRole(@Param("roleName") String roleName);

    // Find all customers - MISSING METHOD FOR RECEPTION CONTROLLER
    @Query("""
    SELECT DISTINCT u FROM User u 
    JOIN u.roles r 
    WHERE r.roleName = 'CUSTOMER'
    AND u.status = 'active' 
    AND u.enabled = true
    ORDER BY u.fullName
    """)
    List<User> findAllCustomers();

    // Find all technicians - MISSING METHOD FOR RECEPTION CONTROLLER
    @Query("""
    SELECT DISTINCT u FROM User u 
    JOIN u.roles r 
    WHERE r.roleName = 'TECHNICIAN'
    AND u.status = 'active' 
    AND u.enabled = true
    ORDER BY u.fullName
    """)
    List<User> findAllTechnicians();

    // Method for UserKPIService
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName AND u.status = 'active'")
    List<User> findByRoles_RoleName(@Param("roleName") String roleName);

    // Methods for Report Service
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.roleName = 'CUSTOMER' AND u.createdAt BETWEEN :startDate AND :endDate")
    Long countNewCustomersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.roleName = 'CUSTOMER' AND u.status = 'active'")
    Long countTotalCustomers();

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.roleName = 'TECHNICIAN' AND u.status = 'active'")
    Long countActiveTechnicians();
}
