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

@WebServlet("/verify")
public class VerifyEmailServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(VerifyEmailServlet.class);
    private final UserService userService = new UserService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String token = request.getParameter("token");
        
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("error", "Token xác thực không hợp lệ");
            request.getRequestDispatcher("/WEB-INF/views/auth/verify.jsp").forward(request, response);
            return;
        }
        
        try {
            if (userService.verifyEmail(token)) {
                request.setAttribute("message", "Xác thực email thành công. Bạn có thể đăng nhập ngay bây giờ.");
            } else {
                request.setAttribute("error", "Token xác thực không hợp lệ hoặc đã hết hạn");
            }
        } catch (SQLException e) {
            logger.error("Error verifying email", e);
            request.setAttribute("error", "Có lỗi xảy ra trong quá trình xác thực");
        }
        
        request.getRequestDispatcher("/WEB-INF/views/auth/verify.jsp").forward(request, response);
    }
} 