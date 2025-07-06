package com.spazone.controller;

import com.spazone.entity.Blog;
import com.spazone.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping
    public String getAllBlogs(Model model) {
        model.addAttribute("blogs", blogService.getAllBlogs());
        return "blog/list";
    }

    @GetMapping("/{id}")
    public String getBlogById(@PathVariable Long id, Model model) {
        Optional<Blog> blog = blogService.getBlogById(id);
        if (blog.isPresent()) {
            model.addAttribute("blog", blog.get());
            return "blog/detail";
        } else {
            return "redirect:/blog";
        }
    }
}
