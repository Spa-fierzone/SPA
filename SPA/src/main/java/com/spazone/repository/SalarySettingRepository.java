package com.spazone.repository;

import com.spazone.entity.SalarySetting;
import com.spazone.entity.SalarySetting.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalarySettingRepository extends JpaRepository<SalarySetting, Long> {
    Optional<SalarySetting> findByRole(Role role);
}