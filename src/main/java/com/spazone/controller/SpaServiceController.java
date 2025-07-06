package com.spazone.controller;

import com.spazone.entity.FavoriteService;
import com.spazone.entity.Service;
import com.spazone.entity.User;
import com.spazone.entity.ServiceRating;
import com.spazone.repository.FavoriteServiceRepository;
import com.spazone.service.SpaServiceService;
import com.spazone.service.UserService;
import com.spazone.service.ServiceRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/services")
public class SpaServiceController {

    @Autowired
    private SpaServiceService spaServiceService;
    @Autowired
    private FavoriteServiceRepository favoriteServiceRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ServiceRatingService serviceRatingService;

    @GetMapping
    public String listServices(Model model, Authentication authentication) {
        List<Service> allServices = spaServiceService.findAllActive();
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByEmail(authentication.getName());
            List<FavoriteService> favorites = favoriteServiceRepository.findByCustomer(user);
            for (Service s : allServices) {
                boolean isFav = favorites.stream().anyMatch(f -> f.getService().getServiceId().equals(s.getServiceId()));
                s.setFavorite(isFav); // You may need to add this field/getter/setter to Service entity
            }
        }
        model.addAttribute("services", allServices);
        return "spa-service/list";
    }

    @GetMapping("/detail/{id}")
    public String viewServiceDetail(@PathVariable("id") Integer id, Model model, Authentication authentication) {
        Service service = spaServiceService.getById(id);
        if (service == null || !"active".equalsIgnoreCase(service.getStatus())) {
            return "redirect:/services";
        }
        model.addAttribute("service", service);
        // Ratings
        List<ServiceRating> ratings = serviceRatingService.getRatingsByService(service);
        model.addAttribute("ratings", ratings);
        boolean hasRated = false;
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByEmail(authentication.getName());
            hasRated = serviceRatingService.hasUserRatedService(user, service);
        }
        model.addAttribute("hasRated", hasRated);
        return "spa-service/detail";
    }

    @PostMapping("/favorite/{id}")
    public String toggleFavorite(@PathVariable("id") Integer serviceId,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để sử dụng tính năng này.");
            return "redirect:/services";
        }
        User user = userService.findByEmail(authentication.getName());
        Service service = spaServiceService.getById(serviceId);
        if (service == null) {
            redirectAttributes.addFlashAttribute("error", "Dịch vụ không tồn tại.");
            return "redirect:/services";
        }
        boolean exists = favoriteServiceRepository.existsByCustomerAndService(user, service);
        if (exists) {
            favoriteServiceRepository.deleteByCustomerAndService(user, service);
        } else {
            favoriteServiceRepository.save(new FavoriteService(user, service));
        }
        return "redirect:/services";
    }

    @PostMapping("/{serviceId}/rate")
    public String rateService(@PathVariable("serviceId") Integer serviceId,
                              @RequestParam("rating") int rating,
                              @RequestParam(value = "comment", required = false) String comment,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để đánh giá.");
            return "redirect:/services/detail/" + serviceId;
        }
        User user = userService.findByEmail(authentication.getName());
        Service service = spaServiceService.getById(serviceId);
        if (service == null) {
            redirectAttributes.addFlashAttribute("error", "Dịch vụ không tồn tại.");
            return "redirect:/services";
        }
        if (serviceRatingService.hasUserRatedService(user, service)) {
            redirectAttributes.addFlashAttribute("error", "Bạn đã đánh giá dịch vụ này.");
            return "redirect:/services/detail/" + serviceId;
        }
        ServiceRating newRating = new ServiceRating();
        newRating.setService(service);
        newRating.setCustomer(user);
        newRating.setRating(rating);
        newRating.setComment(comment);
        serviceRatingService.saveRating(newRating);
        redirectAttributes.addFlashAttribute("success", "Cảm ơn bạn đã đánh giá!");
        return "redirect:/services/detail/" + serviceId;
    }
}
