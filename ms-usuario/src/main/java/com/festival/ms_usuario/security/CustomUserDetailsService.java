package com.festival.ms_usuario.security;

import com.festival.ms_usuario.model.Usuario;
import com.festival.ms_usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException("Usuario no encontrado: " + email));

        
        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + email);
        }

        
        return new User(
            usuario.getEmail(),
            usuario.getPassword(),  // ya esta encriptado con BCrypt en BD
            List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre()))
        );
    }
}