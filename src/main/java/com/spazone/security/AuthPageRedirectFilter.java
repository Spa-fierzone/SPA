package com.spazone.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

public class AuthPageRedirectFilter extends OncePerRequestFilter {

    private static final Set<String> AUTH_URLS = Set.of(
            "/auth/login",
            "/auth/register",
            "/auth/password-reset",
            "/auth/password-reset/request",
            "/auth/verify-email"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() &&
                AUTH_URLS.contains(path) &&
                !path.equals("/logout") &&  // exclude logout
                !"anonymousUser".equals(auth.getPrincipal())) {

            // Đã đăng nhập rồi mà vào trang auth -> redirect về dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
