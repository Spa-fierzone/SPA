package com.spazone.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer serviceId;

    private String name;

    @Column(length = 4000)
    private String description;

    private int duration; // in minutes

    private BigDecimal price;

    private String imageUrl;

    private String status = "active";

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ServiceCategory category;

    @PreUpdate
    public void updateTimestamp() {
        updatedAt = LocalDateTime.now();
    }

    public Service() {
    }

    public Service(Integer serviceId, String name, String description, int duration, BigDecimal price, String imageUrl, String status, LocalDateTime createdAt, LocalDateTime updatedAt, ServiceCategory category) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.price = price;
        this.imageUrl = imageUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.category = category;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDuration() {
        return duration;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public ServiceCategory getCategory() {
        return category;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCategory(ServiceCategory category) {
        this.category = category;
    }
}

