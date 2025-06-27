package com.spazone.security;

import com.spazone.entity.Role;
import com.spazone.entity.User;
import com.spazone.repository.RoleRepository;
import com.spazone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(email.split("@")[0]);
            newUser.setFullName(name);
            newUser.setPassword("OAUTH2_USER");
            newUser.setEnabled(true);
            newUser.setEmailVerified(true);
            newUser.setStatus("active");

            Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                    .orElseGet(() -> roleRepository.save(new Role("CUSTOMER")));
            newUser.setRoles(Set.of(customerRole));

            return userRepository.save(newUser);
        });

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toSet());

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

        return new DefaultOAuth2User(authorities, attributes, "email");
    }
}
