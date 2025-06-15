package com.spazone.entity;

import com.spazone.enums.Gender;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity

@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;// male, female, other

    @Column(length = 4000)
    private String address;

    @Column(length = 20)
    private String status = "active";  // active, inactive, suspended

    @Column(nullable = false)
    private Boolean enabled = false;

    @Column(name = "verification_token", length = 255)
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

    @Column(name = "otp_code", length = 10)
    private String otpCode;

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry;

    @Column(name = "reset_password_token", length = 255)
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiry")
    private LocalDateTime resetPasswordTokenExpiry;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "last_check_in")
    private LocalDateTime lastCheckIn;

    @Column(name = "last_check_out")
    private LocalDateTime lastCheckOut;

    @Column(name = "total_working_hours", precision = 10, scale = 2)
    private BigDecimal totalWorkingHours;

    @Column(name = "performance_score", precision = 5, scale = 2)
    private BigDecimal performanceScore;

    @Column(name = "vip_level", length = 20)
    private String vipLevel = "regular"; // regular, silver, gold, platinum, diamond

    @Column(name = "total_spent", precision = 10, scale = 2)
    private BigDecimal totalSpent;

    @Column(name = "last_visit_date")
    private LocalDateTime lastVisitDate;

    @Column(name = "preferred_branch_id")
    private Integer preferredBranchId;

    @Column(name = "last_password_change")
    private LocalDateTime lastPasswordChange;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;

    @Column(name = "two_factor_enabled")
    private Boolean twoFactorEnabled = false;

    @Column(name = "two_factor_secret", length = 100)
    private String twoFactorSecret;

    @Column(name = "login_attempts")
    private Integer loginAttempts = 0;

    @Column(name = "last_login_attempt")
    private LocalDateTime lastLoginAttempt;

    @Column(name = "profile_picture", length = 255)
    private String profilePicture;

    // Many-to-Many relationship with Role
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public User() {
    }

    public User(Integer userId, String username, String password, String email, String fullName, String phone, LocalDate dateOfBirth, Gender gender, String address, String status, Boolean enabled, String verificationToken, LocalDateTime verificationTokenExpiry, String resetPasswordToken, LocalDateTime resetPasswordTokenExpiry, LocalDateTime lastLogin, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastCheckIn, LocalDateTime lastCheckOut, BigDecimal totalWorkingHours, BigDecimal performanceScore, String vipLevel, BigDecimal totalSpent, LocalDateTime lastVisitDate, Integer preferredBranchId, LocalDateTime lastPasswordChange, Boolean emailVerified, Boolean phoneVerified, Boolean twoFactorEnabled, String twoFactorSecret, Integer loginAttempts, LocalDateTime lastLoginAttempt, String profilePicture, Set<Role> roles) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.status = status;
        this.enabled = enabled;
        this.verificationToken = verificationToken;
        this.verificationTokenExpiry = verificationTokenExpiry;
        this.resetPasswordToken = resetPasswordToken;
        this.resetPasswordTokenExpiry = resetPasswordTokenExpiry;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastCheckIn = lastCheckIn;
        this.lastCheckOut = lastCheckOut;
        this.totalWorkingHours = totalWorkingHours;
        this.performanceScore = performanceScore;
        this.vipLevel = vipLevel;
        this.totalSpent = totalSpent;
        this.lastVisitDate = lastVisitDate;
        this.preferredBranchId = preferredBranchId;
        this.lastPasswordChange = lastPasswordChange;
        this.emailVerified = emailVerified;
        this.phoneVerified = phoneVerified;
        this.twoFactorEnabled = twoFactorEnabled;
        this.twoFactorSecret = twoFactorSecret;
        this.loginAttempts = loginAttempts;
        this.lastLoginAttempt = lastLoginAttempt;
        this.profilePicture = profilePicture;
        this.roles = roles;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public LocalDateTime getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public void setOtpExpiry(LocalDateTime otpExpiry) {
        this.otpExpiry = otpExpiry;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getRoleNames() {
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.joining(", "));
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public Gender getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public LocalDateTime getVerificationTokenExpiry() {
        return verificationTokenExpiry;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public LocalDateTime getResetPasswordTokenExpiry() {
        return resetPasswordTokenExpiry;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getLastCheckIn() {
        return lastCheckIn;
    }

    public LocalDateTime getLastCheckOut() {
        return lastCheckOut;
    }

    public BigDecimal getTotalWorkingHours() {
        return totalWorkingHours;
    }

    public BigDecimal getPerformanceScore() {
        return performanceScore;
    }

    public String getVipLevel() {
        return vipLevel;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public LocalDateTime getLastVisitDate() {
        return lastVisitDate;
    }

    public Integer getPreferredBranchId() {
        return preferredBranchId;
    }

    public LocalDateTime getLastPasswordChange() {
        return lastPasswordChange;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public String getTwoFactorSecret() {
        return twoFactorSecret;
    }

    public Integer getLoginAttempts() {
        return loginAttempts;
    }

    public LocalDateTime getLastLoginAttempt() {
        return lastLoginAttempt;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public void setVerificationTokenExpiry(LocalDateTime verificationTokenExpiry) {
        this.verificationTokenExpiry = verificationTokenExpiry;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public void setResetPasswordTokenExpiry(LocalDateTime resetPasswordTokenExpiry) {
        this.resetPasswordTokenExpiry = resetPasswordTokenExpiry;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setLastCheckIn(LocalDateTime lastCheckIn) {
        this.lastCheckIn = lastCheckIn;
    }

    public void setLastCheckOut(LocalDateTime lastCheckOut) {
        this.lastCheckOut = lastCheckOut;
    }

    public void setTotalWorkingHours(BigDecimal totalWorkingHours) {
        this.totalWorkingHours = totalWorkingHours;
    }

    public void setPerformanceScore(BigDecimal performanceScore) {
        this.performanceScore = performanceScore;
    }

    public void setVipLevel(String vipLevel) {
        this.vipLevel = vipLevel;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public void setLastVisitDate(LocalDateTime lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public void setPreferredBranchId(Integer preferredBranchId) {
        this.preferredBranchId = preferredBranchId;
    }

    public void setLastPasswordChange(LocalDateTime lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }

    public void setLoginAttempts(Integer loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public void setLastLoginAttempt(LocalDateTime lastLoginAttempt) {
        this.lastLoginAttempt = lastLoginAttempt;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}