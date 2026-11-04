package com.festival.ms_evento.security;

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

// Intercepta cada request, valida el JWT y carga la autenticación en el contexto
// No tiene base de datos de usuarios: solo lee email y rol desde el token
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

        // Sin header Bearer: continuar sin autenticar (Spring Security decide después)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (jwtUtil.esValido(token)) {
                String email = jwtUtil.extraerEmail(token);
                String rol = jwtUtil.extraerRol(token);

                log.debug("Request autorizada - usuario: {} rol: {}", email, rol);

                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                    );

                SecurityContextHolder.getContext().setAuthentication(auth);
                chain.doFilter(request, response);

            } else {
                log.warn("Token invalido en request a: {}", request.getRequestURI());
                enviarError(response, "Token invalido o expirado");
            }

        } catch (Exception e) {
            log.error("Error procesando token JWT: {}", e.getMessage());
            enviarError(response, "Token invalido");
        }
    }

    private void enviarError(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + mensaje + "\"}");
    }
}