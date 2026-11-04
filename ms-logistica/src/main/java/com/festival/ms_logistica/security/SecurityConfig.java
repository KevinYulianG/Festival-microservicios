package com.festival.ms_logistica.security;


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
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger sin autenticación
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/v3/api-docs"
                ).permitAll()

               
                .requestMatchers(HttpMethod.POST, "/api/**/zonas/**", "/api/**/escenarios/**", "/api/**/asignaciones/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/**/zonas/**", "/api/**/escenarios/**", "/api/**/asignaciones/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**/zonas/**", "/api/**/escenarios/**", "/api/**/asignaciones/**").hasRole("ADMIN")

               
                .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "STAFF")

              
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}