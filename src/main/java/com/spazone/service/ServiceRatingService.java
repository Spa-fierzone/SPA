package com.spazone.service;

import com.spazone.entity.ServiceRating;
import com.spazone.entity.Service;
import com.spazone.entity.User;
import java.util.List;

public interface ServiceRatingService {
    List<ServiceRating> getRatingsByService(Service service);
    boolean hasUserRatedService(User user, Service service);
    ServiceRating saveRating(ServiceRating rating);
}

