package com.festival.ms_usuario.service;

import com.festival.ms_usuario.dto.LoginDto;
import com.festival.ms_usuario.dto.TokenResponseDto;
import com.festival.ms_usuario.dto.UsuarioDto;
import com.festival.ms_usuario.model.Rol;
import com.festival.ms_usuario.model.Usuario;
import com.festival.ms_usuario.repository.RolRepository;
import com.festival.ms_usuario.repository.UsuarioRepository;
import com.festival.ms_usuario.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public TokenResponseDto login(LoginDto dto) {
        log.info("Intento de login: {}", dto.getEmail());

        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtUtil.generarToken(
            usuario.getEmail(),
            usuario.getRol().getNombre()
        );

        log.info("Login exitoso para: {}", dto.getEmail());
        return new TokenResponseDto(token, usuario.getEmail(), usuario.getRol().getNombre());
    }

    public UsuarioDto registro(UsuarioDto dto) {
        log.info("Registro de nuevo usuario: {}", dto.getEmail());

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya esta registrado");
        }

        Rol rol = rolRepository.findById(dto.getRolId())
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(rol);
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado con ID: {}", guardado.getId());

        UsuarioDto respuesta = new UsuarioDto();
        respuesta.setId(guardado.getId());
        respuesta.setNombre(guardado.getNombre());
        respuesta.setEmail(guardado.getEmail());
        respuesta.setRolId(guardado.getRol().getId());
        return respuesta;
    }
}
