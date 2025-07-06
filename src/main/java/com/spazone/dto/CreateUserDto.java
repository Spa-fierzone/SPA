package com.spazone.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateUserDto {

    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String role;
    private BigDecimal baseSalary;

    public CreateUserDto() {
    }

    public CreateUserDto(String username, String email, String password, String fullName, String phone, String gender, LocalDate dateOfBirth, String address, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.role = role;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
