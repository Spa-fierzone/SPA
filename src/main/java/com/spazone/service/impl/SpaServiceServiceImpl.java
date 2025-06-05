package com.spazone.service.impl;

import com.spazone.entity.Service;
import com.spazone.exception.ResourceNotFoundException;
import com.spazone.repository.ServiceRepository;
import com.spazone.service.SpaServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpaServiceServiceImpl implements SpaServiceService {
    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public Page<Service> searchByName(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return serviceRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }
        return serviceRepository.findAll(pageable);
    }

    @Override
    public Service getById(Integer id) {
        return serviceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Service not found"));
    }

    @Override
    public Service save(Service service) {
        return serviceRepository.save(service);
    }

    @Override
    public void deleteById(Integer id) {
        serviceRepository.deleteById(id);
    }

    @Override
    public List<Service> getTop6() {
        return serviceRepository.findTop6ByStatusOrderByCreatedAtDesc("active");
    }

    @Override
    public List<Service> findAllActive() {
        return serviceRepository.findByStatusOrderByCreatedAtDesc("active");
    }
}
