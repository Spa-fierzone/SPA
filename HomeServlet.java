package com.mycompany.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/dashboard")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Nếu đã đăng nhập, chuyển hướng đến dashboard tương ứng
        if (session != null && session.getAttribute("user") != null) {
            String role = (String) session.getAttribute("userRole");
            if (role != null) {
                switch (role.toLowerCase()) {
                    case "admin":
                        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                        return;
                    case "manager":
                        response.sendRedirect(request.getContextPath() + "/manager/dashboard");
                        return;
                    case "technician":
                        response.sendRedirect(request.getContextPath() + "/technician/dashboard");
                        return;
                    case "receptionist":
                        response.sendRedirect(request.getContextPath() + "/receptionist/dashboard");
                        return;
                    default:
                        response.sendRedirect(request.getContextPath() + "/customer/dashboard");
                        return;
                }
            }
        }
        // Nếu chưa đăng nhập, hiển thị trang chủ
        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
} 