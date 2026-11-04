package com.festival.ms_usuario.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        
        String authHeader = request.getHeader("Authorization");

        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        
        String token = authHeader.substring(7);

        if (jwtUtil.esValido(token)) {
            String email = jwtUtil.extraerEmail(token);
            String rol = jwtUtil.extraerRol(token);

            log.debug("Token valido para usuario: {} con rol: {}", email, rol);

            
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                );

            
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            log.warn("Token JWT invalido o expirado");
        }

        chain.doFilter(request, response);
    }
}