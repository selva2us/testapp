package com.supermarket.pos_backend.security;

import com.supermarket.pos_backend.repository.AdminUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAdminFilter extends OncePerRequestFilter {

    private final AdminUserRepository adminRepository;

    public JwtAdminFilter(AdminUserRepository adminRepository) {
        this.adminRepository = adminRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain)
            throws ServletException, IOException {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
                String email = auth.getName();
                adminRepository.findByEmail(email).ifPresent(AdminContext::setCurrentUser);
            }

            filterChain.doFilter(request, response);
        } finally {
            AdminContext.clear();
        }
    }
}
