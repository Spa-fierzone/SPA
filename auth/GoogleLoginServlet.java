package com.mycompany.servlet.auth;

import com.mycompany.util.OAuth2Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/auth/google")
public class GoogleLoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(GoogleLoginServlet.class);
    private final OAuth2Util oauth2Util = new OAuth2Util();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Generate state parameter for CSRF protection
            String state = oauth2Util.generateState();
            request.getSession().setAttribute("oauth_state", state);
            
            // Build Google OAuth URL
            String authUrl = oauth2Util.buildGoogleAuthUrl(state);
            
            // Redirect to Google login page
            response.sendRedirect(authUrl);
        } catch (Exception e) {
            logger.error("Error initiating Google login", e);
            request.setAttribute("error", "Không thể kết nối với Google. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
} 