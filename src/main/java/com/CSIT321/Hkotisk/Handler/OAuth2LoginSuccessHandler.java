package com.CSIT321.Hkotisk.Handler;

import com.CSIT321.Hkotisk.Entity.User;
import com.CSIT321.Hkotisk.Repository.UserRepository;
import com.CSIT321.Hkotisk.Service.JWTService;
import com.CSIT321.Hkotisk.Service.MyUserDetailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MyUserDetailService userDetailService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            response.sendRedirect("http://localhost:5173/oauth2/error?message=Invalid authentication");
            return;
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = getEmail(attributes);
        String name = getName(attributes);

        if (email == null) {
            response.sendRedirect("http://localhost:5173/oauth2/error?message=Email not found");
            return;
        }

        // Check if user exists or create a new one
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setRole("staff"); // Default role
                    newUser.setEnabled(true);
                    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    return userRepository.save(newUser);
                });

        // Generate JWT token
        UserDetails userDetails = userDetailService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        // Redirect to frontend with token and user details
        String redirectUrl = "http://localhost:5173/oauth2/redirect?token=" + token + 
                    "&role=" + user.getRole() + 
                    "&email=" + user.getEmail() + 
                    "&name=" + user.getUsername();
        response.sendRedirect(redirectUrl);
    }

    private String getEmail(Map<String, Object> attributes) {
        System.out.println("Attributes: "+attributes);
        if (attributes.containsKey("email")) {
            return (String) attributes.get("email"); // Google
        } else if (attributes.containsKey("preferred_username")) {
            return (String) attributes.get("preferred_username"); // Microsoft
        }
        return null; // Email not found
    }

    private String getName(Map<String, Object> attributes) {
        if (attributes.containsKey("name")) {
            return (String) attributes.get("name"); // Google & Microsoft
        } else if (attributes.containsKey("given_name")) {
            return (String) attributes.get("given_name"); // Alternative Microsoft format
        }
        return "Unknown User";
    }
}
