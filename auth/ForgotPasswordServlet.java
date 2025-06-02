package com.mycompany.servlet.auth;

import com.mycompany.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordServlet.class);
    private final UserService userService = new UserService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        
        try {
            if (userService.requestPasswordReset(email)) {
                request.setAttribute("message", 
                    "Yêu cầu đặt lại mật khẩu đã được gửi đến email của bạn. " +
                    "Vui lòng kiểm tra email và làm theo hướng dẫn.");
            } else {
                request.setAttribute("error", 
                    "Không tìm thấy tài khoản với email này hoặc tài khoản chưa được kích hoạt.");
            }
        } catch (SQLException e) {
            logger.error("Error requesting password reset", e);
            request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau.");
        }
        
        request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request, response);
    }
} 