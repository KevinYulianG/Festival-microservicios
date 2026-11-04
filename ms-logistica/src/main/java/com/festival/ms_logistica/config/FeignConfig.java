package com.festival.ms_logistica.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(5000, 10000);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            switch (response.status()) {
                case 404:
                    return new RuntimeException(
                            "Recurso no encontrado en servicio externo: " + methodKey);
                case 400:
                    return new RuntimeException(
                            "Solicitud inválida al servicio externo: " + methodKey);
                case 403:
                    return new RuntimeException(
                            "Acceso denegado en servicio externo: " + methodKey);
                case 500:
                    return new RuntimeException(
                            "Error interno en servicio externo: " + methodKey);
                default:
                    return new RuntimeException(
                            "Error al comunicarse con servicio externo: "
                                    + methodKey + " status: " + response.status());
            }
        };
    }
}