package com.spazone.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "salary_settings")
public class SalarySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    private Double commissionPerCustomer;
    private Double commissionPerService;

    private Double attendanceBonus;
    private Double revenueBonus;
    private Double ratingBonus;

    private Double penalty;

    public enum Role {
        RECEPTIONIST,
        TECHNICIAN,
        MANAGER,
    }

    public SalarySetting() {
    }

    public SalarySetting(Long id, Role role, Double commissionPerCustomer, Double commissionPerService,
                        Double attendanceBonus, Double revenueBonus, Double ratingBonus,
                        Double penalty) {
        this.id = id;
        this.role = role;
        this.commissionPerCustomer = commissionPerCustomer;
        this.commissionPerService = commissionPerService;
        this.attendanceBonus = attendanceBonus;
        this.revenueBonus = revenueBonus;
        this.ratingBonus = ratingBonus;
        this.penalty = penalty;
    }

    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public Double getCommissionPerCustomer() {
        return commissionPerCustomer;
    }

    public Double getCommissionPerService() {
        return commissionPerService;
    }

    public Double getAttendanceBonus() {
        return attendanceBonus;
    }

    public Double getRevenueBonus() {
        return revenueBonus;
    }

    public Double getRatingBonus() {
        return ratingBonus;
    }

    public Double getPenalty() {
        return penalty;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setCommissionPerCustomer(Double commissionPerCustomer) {
        this.commissionPerCustomer = commissionPerCustomer;
    }

    public void setCommissionPerService(Double commissionPerService) {
        this.commissionPerService = commissionPerService;
    }

    public void setAttendanceBonus(Double attendanceBonus) {
        this.attendanceBonus = attendanceBonus;
    }

    public void setRevenueBonus(Double revenueBonus) {
        this.revenueBonus = revenueBonus;
    }

    public void setRatingBonus(Double ratingBonus) {
        this.ratingBonus = ratingBonus;
    }

    public void setPenalty(Double penalty) {
        this.penalty = penalty;
    }

}
