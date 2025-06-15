package com.spazone.repository;

import com.spazone.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
    Page<Service> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Service> findTop6ByStatusOrderByCreatedAtDesc(String status);
    List<Service> findByStatusOrderByCreatedAtDesc(String status);

}


