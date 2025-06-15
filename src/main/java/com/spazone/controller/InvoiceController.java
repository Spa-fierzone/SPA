package com.spazone.controller;

import com.spazone.entity.Invoice;
import com.spazone.entity.User;
import com.spazone.service.InvoiceService;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private UserService userService;

    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    @GetMapping("/my")
    public String viewMyInvoices(Model model, Authentication authentication) {
        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }
        String username = authentication.getName();
        User customer = userService.findByEmail(username);
        List<Invoice> invoices = invoiceService.getByCustomer(customer);
        model.addAttribute("invoices", invoices);
        return "invoice/view";
    }

    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable Integer id, Model model) {
        Invoice invoice = invoiceService.getById(id);
        if (invoice == null) {
            return "redirect:/invoices";
        }
        model.addAttribute("invoice", invoice);
        return "invoice/detail";
    }
}
