package com.spazone.security;

import com.spazone.entity.Role;
import com.spazone.entity.User;
import com.spazone.enums.Gender;
import com.spazone.repository.RoleRepository;
import com.spazone.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(email.split("@")[0]);
            newUser.setFullName(name);
            newUser.setPassword("OAUTH2_USER"); // No password for OAuth2 users
            newUser.setEnabled(true);
            newUser.setGender(Gender.MALE);
            newUser.setEmailVerified(true);
            newUser.setStatus("active");

            // Assign default role
            Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                    .orElseGet(() -> roleRepository.save(new Role("CUSTOMER")));
            newUser.setRoles(Set.of(customerRole));

            return userRepository.save(newUser);
        });

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Tạo GrantedAuthority mới từ roles DB
        Set<GrantedAuthority> updatedAuthorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toSet());

        // Tạo Authentication mới (principal có thể dùng user hoặc OAuth2User)
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                updatedAuthorities);

        // Thay thế Authentication hiện tại trong SecurityContext
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // Tiếp tục xử lý mặc định
        super.onAuthenticationSuccess(request, response, newAuth);
    }
}