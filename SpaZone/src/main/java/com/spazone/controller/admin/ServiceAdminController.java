package com.spazone.controller.admin;

import com.spazone.entity.Service;
import com.spazone.repository.ServiceCategoryRepository;
import com.spazone.service.CloudinaryService;
import com.spazone.service.SpaServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/services")
public class ServiceAdminController {

    @Autowired
    private SpaServiceService serviceService;

    @Autowired
    private ServiceCategoryRepository categoryRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public String listServices(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        int size = 5;
        Page<Service> servicePage = serviceService.searchByName(keyword, page, size);

        model.addAttribute("services", servicePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", servicePage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "admin/services/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("service", new Service());
        model.addAttribute("categories", categoryRepo.findAll());
        return "admin/services/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("service") Service service,
                       @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(imageFile);
            service.setImageUrl(imageUrl);
        } else if (service.getServiceId() != null) {
            Service existing = serviceService.getById(service.getServiceId());
            service.setImageUrl(existing.getImageUrl());
        }
        serviceService.save(service);
        return "redirect:/admin/services";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("service", serviceService.getById(id));
        model.addAttribute("categories", categoryRepo.findAll());
        return "admin/services/form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        serviceService.deleteById(id);
        return "redirect:/admin/services";
    }
}