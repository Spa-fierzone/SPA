package com.spazone.service;

import com.spazone.entity.Service;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SpaServiceService {
    Page<Service> searchByName(String keyword, int page, int size);
    Service getById(Integer id);
    Service save(Service service);
    void deleteById(Integer id);

    List<Service> getTop6();
    List<Service> findAllActive();
}
