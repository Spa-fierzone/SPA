package com.spazone.service.impl;

import com.spazone.entity.ServiceRating;
import com.spazone.entity.Service;
import com.spazone.entity.User;
import com.spazone.repository.ServiceRatingRepository;
import com.spazone.service.ServiceRatingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceRatingServiceImpl implements ServiceRatingService {
    @Autowired
    private ServiceRatingRepository serviceRatingRepository;

    @Override
    public List<ServiceRating> getRatingsByService(Service service) {
        return serviceRatingRepository.findByService(service);
    }

    @Override
    public boolean hasUserRatedService(User user, Service service) {
        return serviceRatingRepository.existsByServiceAndCustomer(service, user);
    }

    @Override
    public ServiceRating saveRating(ServiceRating rating) {
        return serviceRatingRepository.save(rating);
    }
}

