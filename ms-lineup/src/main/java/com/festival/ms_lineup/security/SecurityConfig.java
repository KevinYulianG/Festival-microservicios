package com.festival.ms_lineup.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()

                // Solo ADMIN puede crear artistas
                .requestMatchers(HttpMethod.POST, "/api/artistas")
                    .hasRole("ADMIN")

                // Solo ADMIN puede actualizar artistas
                .requestMatchers(HttpMethod.PUT, "/api/artistas/**")
                    .hasRole("ADMIN")

                // Solo ADMIN puede desactivar artistas
                .requestMatchers(HttpMethod.DELETE, "/api/artistas/**")
                    .hasRole("ADMIN")

                // Solo ADMIN puede programar artistas
                .requestMatchers(HttpMethod.POST, "/api/programaciones")
                    .hasRole("ADMIN")

                // Solo ADMIN puede modificar programaciones
                .requestMatchers(HttpMethod.PUT, "/api/programaciones/**")
                    .hasRole("ADMIN")

                // Consultas disponibles para todos los roles
                .requestMatchers(HttpMethod.GET, "/api/artistas/**")
                    .hasAnyRole("ASISTENTE", "STAFF", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/programaciones/**")
                    .hasAnyRole("ASISTENTE", "STAFF", "ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}