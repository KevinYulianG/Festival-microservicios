package com.festival.ms_lineup.config;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            5000, TimeUnit.MILLISECONDS,
            10000, TimeUnit.MILLISECONDS,
            true
        );
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