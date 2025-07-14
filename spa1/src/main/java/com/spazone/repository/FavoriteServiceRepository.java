package com.spazone.repository;

import com.spazone.entity.FavoriteService;
import com.spazone.entity.User;
import com.spazone.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteServiceRepository extends JpaRepository<FavoriteService, Integer> {
    List<FavoriteService> findByCustomer(User customer);
    List<FavoriteService> findByCustomerEmail(String email);
    boolean existsByCustomerAndService(User customer, Service service);
    void deleteByCustomerAndService(User customer, Service service);
}

