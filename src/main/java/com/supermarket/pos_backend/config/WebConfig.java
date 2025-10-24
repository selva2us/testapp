package com.supermarket.pos_backend.config;

import com.supermarket.pos_backend.security.CurrentAdminResolver;
import com.supermarket.pos_backend.security.CurrentStaffResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final CurrentAdminResolver currentAdminResolver;
    private final CurrentStaffResolver currentStaffResolver;

    public WebConfig(CurrentAdminResolver currentAdminResolver, CurrentStaffResolver currentStaffResolver) {
        this.currentAdminResolver = currentAdminResolver;
        this.currentStaffResolver = currentStaffResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentAdminResolver);
        resolvers.add(currentStaffResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}

