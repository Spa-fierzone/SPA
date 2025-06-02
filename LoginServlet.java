package com.mycompany.servlet;

import com.mycompany.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private final UserService userService = new UserService();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {
            userService.login(username, password).ifPresentOrElse(
                user -> {
                    // Login successful
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    session.setAttribute("userId", user.getId());
                    session.setAttribute("userRole", user.getRole());
                    
                    // Redirect based on role
                    try {
                        switch (user.getRole().toLowerCase()) {
                            case "admin":
                                response.sendRedirect("/admin/dashboard");
                                break;
                            case "manager":
                                response.sendRedirect("/manager/dashboard");
                                break;
                            case "technician":
                                response.sendRedirect("/technician/dashboard");
                                break;
                            case "receptionist":
                                response.sendRedirect("/receptionist/dashboard");
                                break;
                            default:
                                response.sendRedirect("/customer/dashboard");
                        }
                    } catch (IOException e) {
                        logger.error("Error redirecting after login", e);
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    // Login failed
                    try {
                        request.setAttribute("error", "Invalid username or password");
                        request.getRequestDispatcher("/login.jsp").forward(request, response);
                    } catch (ServletException | IOException e) {
                        logger.error("Error forwarding to login page", e);
                        throw new RuntimeException(e);
                    }
                }
            );
        } catch (Exception e) {
            logger.error("Error during login", e);
            request.setAttribute("error", "An error occurred during login. Please try again.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
} 