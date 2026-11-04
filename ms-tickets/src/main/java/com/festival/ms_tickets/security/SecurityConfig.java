package com.festival.ms_tickets.security;

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

                // ASISTENTE y ADMIN pueden comprar tickets
                .requestMatchers(HttpMethod.POST, "/api/tickets")
                    .hasAnyRole("ASISTENTE", "ADMIN")

                // STAFF y ADMIN validan entradas en puerta
                .requestMatchers(HttpMethod.PUT, "/api/tickets/validar/**")
                    .hasAnyRole("STAFF", "ADMIN")

                // Solo ADMIN puede cancelar tickets
                .requestMatchers(HttpMethod.PUT, "/api/tickets/cancelar/**")
                    .hasRole("ADMIN")

                // Consultas para todos los roles autenticados
                .requestMatchers(HttpMethod.GET, "/api/tickets/**")
                    .hasAnyRole("ASISTENTE", "STAFF", "ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}