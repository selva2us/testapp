package com.supermarket.pos_backend.security;

import com.supermarket.pos_backend.annotations.CurrentStaff;
import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.StaffUser;
import com.supermarket.pos_backend.repository.StaffUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurrentStaffResolver implements HandlerMethodArgumentResolver {
    private final StaffUserRepository staffRepo;
    private final JwtUtil jwtUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentStaff.class) != null &&
                parameter.getParameterType().equals(StaffUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        String email = SecurityUtils.getUser(parameter,mavContainer,webRequest,binderFactory,jwtUtil);
        if (email == null) return null;

        Optional<StaffUser> staffOpt = staffRepo.findByEmail(email);
        if (staffOpt.isPresent()) {
            return staffOpt.get();
        }

        // Handle 'required = false'
        boolean required = parameter.getParameterAnnotation(CurrentStaff.class).required();
        if (!required) {
            return null;
        }

        throw new RuntimeException("Staff not found for logged-in user");

    }
}
