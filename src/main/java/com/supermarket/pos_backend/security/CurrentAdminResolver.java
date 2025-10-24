package com.supermarket.pos_backend.security;

import com.supermarket.pos_backend.annotations.CurrentAdmin;
import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.repository.AdminUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentAdminResolver implements HandlerMethodArgumentResolver {

    private final AdminUserRepository adminUserRepository;
    private final JwtUtil jwtUtil;

    public CurrentAdminResolver(AdminUserRepository adminUserRepository, JwtUtil jwtUtil) {
        this.adminUserRepository = adminUserRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentAdmin.class) != null &&
                parameter.getParameterType().equals(AdminUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            return adminUserRepository.findByEmail(email).orElse(null);
        }
        return null;
    }
}
