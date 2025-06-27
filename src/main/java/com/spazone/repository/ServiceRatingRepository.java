package com.spazone.repository;

import com.spazone.entity.ServiceRating;
import com.spazone.entity.Service;
import com.spazone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRatingRepository extends JpaRepository<ServiceRating, Integer> {
    List<ServiceRating> findByService(Service service);
    List<ServiceRating> findByCustomer(User customer);
    boolean existsByServiceAndCustomer(Service service, User customer);
    List<ServiceRating> findByCustomerEmail(String email);
}
