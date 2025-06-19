package com.spazone.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_categories")
public class ServiceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    private String name;

    @Column(length = 4000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private ServiceCategory parentCategory;

    private LocalDateTime createdAt = LocalDateTime.now();

    public ServiceCategory() {
    }

    public ServiceCategory(Integer categoryId, String name, String description, ServiceCategory parentCategory, LocalDateTime createdAt) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.parentCategory = parentCategory;
        this.createdAt = createdAt;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ServiceCategory getParentCategory() {
        return parentCategory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParentCategory(ServiceCategory parentCategory) {
        this.parentCategory = parentCategory;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

