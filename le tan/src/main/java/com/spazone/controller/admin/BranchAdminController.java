package com.spazone.controller.admin;

import com.spazone.entity.Branch;
import com.spazone.entity.User;
import com.spazone.repository.UserRepository;
import com.spazone.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/branches")
public class BranchAdminController {

    @Autowired
    private BranchService branchService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listBranches(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Integer managerId) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Branch> branches = branchService.searchBranches(keyword, managerId, pageable);

        List<User> managers = userRepository.findUsersByRole("MANAGER");

        Map<Integer, String> managerNameMap = managers.stream()
                .collect(Collectors.toMap(User::getUserId, User::getFullName));

        model.addAttribute("branches", branches.getContent());
        model.addAttribute("totalPages", branches.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("managerId", managerId);
        model.addAttribute("managers", managers);
        model.addAttribute("managerNameMap", managerNameMap); // thêm vào model

        return "admin/branches/list";
    }


    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("branch", new Branch());
        model.addAttribute("managers", userRepository.findUsersByRole("MANAGER"));
        return "admin/branches/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Branch branch = branchService.getById(id);
        if (branch == null) return "redirect:/admin/branches";
        model.addAttribute("branch", branch);
        model.addAttribute("managers", userRepository.findUsersByRole("MANAGER"));
        return "admin/branches/form";
    }

    @PostMapping("/save")
    public String saveBranch(@ModelAttribute Branch branch, @RequestParam Integer managerId) {
        branch.setManagerId(managerId);
        branchService.save(branch);
        return "redirect:/admin/branches";
    }

    @PostMapping("/change-status/{id}")
    public String changeStatus(@PathVariable Integer id) {
        branchService.toggleStatus(id);
        return "redirect:/admin/branches";
    }
}
