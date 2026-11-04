package com.festival.ms_logistica.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorizationHeader = request.getHeader("Authorization");
            
            // Toma el token JWT del usuario y se lo inyecta a la llamada de Feign
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                template.header("Authorization", authorizationHeader);
            }
        }
    }
}