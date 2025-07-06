package com.spazone.controller;

import com.spazone.entity.Room;
import com.spazone.service.RoomService;
import com.spazone.service.BranchService;
import com.spazone.entity.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/rooms")
public class RoomManagementController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private BranchService branchService;

    @GetMapping
    public String listRooms(Model model) {
        List<Room> rooms = roomService.findAll();
        model.addAttribute("rooms", rooms);
        return "admin/rooms";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("room", new Room());
        List<Branch> branches = branchService.findAllActive();
        model.addAttribute("branches", branches);
        return "admin/room-form";
    }

    @PostMapping("/create")
    public String createRoom(@ModelAttribute Room room, @RequestParam("branch.id") Integer branchId, RedirectAttributes redirectAttributes) {
        Branch branch = branchService.getById(branchId);
        room.setBranch(branch);
        roomService.save(room);
        redirectAttributes.addFlashAttribute("successMessage", "Tạo phòng thành công!");
        return "redirect:/admin/rooms";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Room room = roomService.findById(id);
        model.addAttribute("room", room);
        List<Branch> branches = branchService.findAllActive();
        model.addAttribute("branches", branches);
        return "admin/room-form";
    }

    @PostMapping("/edit/{id}")
    public String updateRoom(@PathVariable Integer id, @ModelAttribute Room room, @RequestParam("branch.id") Integer branchId, RedirectAttributes redirectAttributes) {
        room.setId(id);
        Branch branch = branchService.getById(branchId);
        room.setBranch(branch);
        roomService.save(room);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật phòng thành công!");
        return "redirect:/admin/rooms";
    }

    @PostMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        roomService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa phòng thành công!");
        return "redirect:/admin/rooms";
    }
}
