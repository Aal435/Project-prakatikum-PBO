package com.example.crud.security;

import com.example.crud.model.User;
import com.example.crud.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
                                        throws IOException, ServletException {
        // Ambil user berdasarkan username yang login
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        // Redirect berdasarkan role
        if (user.getRole() == 1) {
            response.sendRedirect("/index");
        } else {
            response.sendRedirect("/index"); // arahkan ke controller
        }
    }
}
