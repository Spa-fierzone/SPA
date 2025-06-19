
package com.spazone.service.impl;

import com.spazone.entity.SalarySetting;
import com.spazone.entity.SalarySetting.Role;
import com.spazone.repository.SalarySettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalarySettingService {

    @Autowired
    private SalarySettingRepository salarySettingRepository;

    public List<SalarySetting> findAll() {
        return salarySettingRepository.findAll();
    }

    public Optional<SalarySetting> findByRole(Role role) {
        return salarySettingRepository.findByRole(role);
    }

    public SalarySetting save(SalarySetting setting) {
        return salarySettingRepository.save(setting);
    }
}