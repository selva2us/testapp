package com.supermarket.pos_backend.security;

import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.StaffUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.HashMap;
import java.util.Map;

public class SecurityUtils {
    public static String getUser(MethodParameter parameter,
                                 ModelAndViewContainer mavContainer,
                                 NativeWebRequest webRequest,
                                 WebDataBinderFactory binderFactory, JwtUtil jwtUtil) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractEmail(token);
        }
        return null;
    }
    public static Map<String, Long> getCurrentUserId(AdminUser admin, StaffUser staff){
        Long adminId;
        Long staffId = null;

        if (admin != null && staff != null)  {
            adminId = admin.getId();
            staffId = staff.getId();
        } else if(admin != null) {
            adminId = admin.getId();
        } else if (staff != null) {
            adminId = staff.getAdmin().getId();
            staffId = staff.getId();
        } else {
            throw new RuntimeException("Unauthorized user");
        }
            Map<String, Long> ids = new HashMap<>();
            ids.put("adminId", adminId);
            ids.put("staffId", staffId);
            return ids;
        }
}
