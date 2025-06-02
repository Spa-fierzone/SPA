package com.mycompany.servlet.auth;

import com.mycompany.model.User;
import com.mycompany.model.GoogleUserInfo;
import com.mycompany.service.UserService;
import com.mycompany.util.OAuth2Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/auth/google/callback")
public class GoogleCallbackServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(GoogleCallbackServlet.class);
    private final UserService userService = new UserService();
    private final OAuth2Util oauth2Util = new OAuth2Util();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        // Verify state parameter
        String state = request.getParameter("state");
        String savedState = (String) session.getAttribute("oauth_state");
        if (state == null || !state.equals(savedState)) {
            request.setAttribute("error", "Invalid state parameter");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Clear state from session
        session.removeAttribute("oauth_state");
        
        String code = request.getParameter("code");
        if (code == null) {
            request.setAttribute("error", "Authorization code not received");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }
        
        try {
            // Exchange code for tokens
            String accessToken = oauth2Util.getGoogleAccessToken(code);
            
            // Get user info from Google
            GoogleUserInfo googleUser = oauth2Util.getGoogleUserInfo(accessToken);
            
            // Check if user exists
            Optional<User> existingUser = userService.getUserByEmail(googleUser.getEmail());
            
            if (existingUser.isPresent()) {
                // User exists, log them in
                User user = existingUser.get();
                if (!"active".equals(user.getStatus())) {
                    request.setAttribute("error", "Tài khoản của bạn chưa được kích hoạt");
                    request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                    return;
                }
                
                // Update last login
                userService.updateLastLogin(user.getId());
                
                // Set session attributes
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("userRole", user.getRole());
                
                // Redirect to appropriate dashboard
                redirectToDashboard(request, response, user.getRole());
            } else {
                // Create new user
                User newUser = new User();
                newUser.setEmail(googleUser.getEmail());
                newUser.setFullName(googleUser.getName());
                newUser.setUsername(googleUser.getEmail().split("@")[0]); // Use email prefix as username
                newUser.setRole("customer"); // Default role
                newUser.setStatus("active"); // Auto-activate Google accounts
                newUser.setAvatar(googleUser.getPicture());
                
                // Generate random password
                String randomPassword = oauth2Util.generateRandomPassword();
                newUser.setPassword(randomPassword);
                
                // Register user
                userService.registerGoogleUser(newUser, googleUser.getId());
                
                // Set session attributes
                session.setAttribute("user", newUser);
                session.setAttribute("userId", newUser.getId());
                session.setAttribute("userRole", newUser.getRole());
                
                // Redirect to dashboard
                redirectToDashboard(request, response, newUser.getRole());
            }
        } catch (Exception e) {
            logger.error("Error processing Google callback", e);
            request.setAttribute("error", "Có lỗi xảy ra trong quá trình đăng nhập với Google");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
    
    private void redirectToDashboard(HttpServletRequest request, HttpServletResponse response, String role) throws IOException {
        String contextPath = request.getContextPath();
        switch (role.toLowerCase()) {
            case "admin":
                response.sendRedirect(contextPath + "/admin/dashboard");
                break;
            case "manager":
                response.sendRedirect(contextPath + "/manager/dashboard");
                break;
            case "technician":
                response.sendRedirect(contextPath + "/technician/dashboard");
                break;
            case "receptionist":
                response.sendRedirect(contextPath + "/receptionist/dashboard");
                break;
            default:
                response.sendRedirect(contextPath + "/customer/dashboard");
        }
    }
} 