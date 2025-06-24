package com.spazone.controller.manager;

import com.spazone.entity.Service;
import com.spazone.entity.ServiceCategory;
import com.spazone.repository.ServiceCategoryRepository;
import com.spazone.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager")
public class ServiceManagementController {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @GetMapping("/services")
    public String showServiceList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Service> servicePage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            servicePage = serviceRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
        } else {
            servicePage = serviceRepository.findAll(pageable);
        }

        // Lọc theo status và category nếu cần
        // TODO: Implement custom query methods for filtering

        List<ServiceCategory> categories = serviceCategoryRepository.findAll();

        model.addAttribute("services", servicePage.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", servicePage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("categoryId", categoryId);

        return "manager/service-management";
    }

    @GetMapping("/services/create")
    public String showCreateForm(Model model) {
        List<ServiceCategory> categories = serviceCategoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("service", new Service());
        return "manager/service-form";
    }

    @PostMapping("/services/create")
    public String createService(@ModelAttribute Service service,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            // Validate input
            if (service.getName() == null || service.getName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Tên dịch vụ không được để trống!");
                return "redirect:/manager/services/create";
            }

            if (service.getDescription() == null || service.getDescription().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Mô tả dịch vụ không được để trống!");
                return "redirect:/manager/services/create";
            }

            if (service.getPrice() == null || service.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "Giá dịch vụ phải lớn hơn 0!");
                return "redirect:/manager/services/create";
            }

            if (service.getDuration() <= 0) {
                redirectAttributes.addFlashAttribute("error", "Thời gian dịch vụ phải lớn hơn 0!");
                return "redirect:/manager/services/create";
            }

            if (service.getCategory() == null) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn danh mục dịch vụ!");
                return "redirect:/manager/services/create";
            }

            // Set default values
            service.setCreatedAt(LocalDateTime.now());
            service.setUpdatedAt(LocalDateTime.now());
            if (service.getStatus() == null || service.getStatus().isEmpty()) {
                service.setStatus("active");
            }

            // Validate category exists
            if (service.getCategory().getCategoryId() != null) {
                Optional<ServiceCategory> categoryOpt = serviceCategoryRepository.findById(service.getCategory().getCategoryId());
                if (categoryOpt.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Danh mục được chọn không tồn tại!");
                    return "redirect:/manager/services/create";
                }
                service.setCategory(categoryOpt.get());
            }

            serviceRepository.save(service);
            redirectAttributes.addFlashAttribute("message", "Tạo dịch vụ thành công!");
            return "redirect:/manager/services";

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ: " + e.getMessage());
            return "redirect:/manager/services/create";
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi để debug
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tạo dịch vụ: " + e.getMessage());
            return "redirect:/manager/services/create";
        }
    }
    @GetMapping("/services/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Service> serviceOpt = serviceRepository.findById(id);
        if (serviceOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy dịch vụ!");
            return "redirect:/manager/services";
        }

        List<ServiceCategory> categories = serviceCategoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("service", serviceOpt.get());
        model.addAttribute("isEdit", true);
        return "manager/service-form";
    }

    @PostMapping("/services/{id}/edit")
    public String updateService(@PathVariable Integer id,
                                @ModelAttribute Service service,
                                RedirectAttributes redirectAttributes) {
        try {
            Optional<Service> existingServiceOpt = serviceRepository.findById(id);
            if (existingServiceOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy dịch vụ!");
                return "redirect:/manager/services";
            }

            Service existingService = existingServiceOpt.get();
            existingService.setName(service.getName());
            existingService.setDescription(service.getDescription());
            existingService.setPrice(service.getPrice());
            existingService.setDuration(service.getDuration());
            existingService.setCategory(service.getCategory());
            existingService.setImageUrl(service.getImageUrl());
            existingService.setStatus(service.getStatus());
            existingService.setUpdatedAt(LocalDateTime.now());

            serviceRepository.save(existingService);
            redirectAttributes.addFlashAttribute("message", "Cập nhật dịch vụ thành công!");
            return "redirect:/manager/services";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật dịch vụ: " + e.getMessage());
            return "redirect:/manager/services/" + id + "/edit";
        }
    }

    @PostMapping("/services/{id}/status")
    public String changeServiceStatus(@PathVariable Integer id,
                                      @RequestParam String status,
                                      RedirectAttributes redirectAttributes) {
        try {
            Optional<Service> serviceOpt = serviceRepository.findById(id);
            if (serviceOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy dịch vụ!");
                return "redirect:/manager/services";
            }

            Service service = serviceOpt.get();
            service.setStatus(status);
            service.setUpdatedAt(LocalDateTime.now());
            serviceRepository.save(service);

            redirectAttributes.addFlashAttribute("message", "Đã cập nhật trạng thái dịch vụ!");
            return "redirect:/manager/services";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/manager/services";
        }
    }

    @PostMapping("/services/{id}/delete")
    public String deleteService(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Service> serviceOpt = serviceRepository.findById(id);
            if (serviceOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy dịch vụ!");
                return "redirect:/manager/services";
            }

            serviceRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa dịch vụ thành công!");
            return "redirect:/manager/services";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa dịch vụ: " + e.getMessage());
            return "redirect:/manager/services";
        }
    }
}