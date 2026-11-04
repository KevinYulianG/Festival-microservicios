package com.festival.ms_usuario.service;

import com.festival.ms_usuario.client.EventoClient;
import com.festival.ms_usuario.client.NotificacionClient;
import com.festival.ms_usuario.dto.EventoDto;
import com.festival.ms_usuario.dto.NotificacionDto;
import com.festival.ms_usuario.dto.UsuarioDto;
import com.festival.ms_usuario.mapper.UsuarioMapper;
import com.festival.ms_usuario.model.Rol;
import com.festival.ms_usuario.model.Usuario;
import com.festival.ms_usuario.repository.RolRepository;
import com.festival.ms_usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final NotificacionClient notificacionClient;
    private final EventoClient eventoClient;

    public List<UsuarioDto> listarActivos() {
        log.info("Listando todos los usuarios activos");
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDto buscarPorId(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con ID: {}", id);
                    return new RuntimeException("Usuario no encontrado con ID: " + id);
                });
        return usuarioMapper.toDTO(usuario);
    }

    public UsuarioDto crear(UsuarioDto dto) {
        log.info("Creando usuario con email: {}", dto.getEmail());

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email ya registrado: {}", dto.getEmail());
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.getEmail());
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + dto.getRolId()));

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(rol);

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID: {}", guardado.getId());

        // Notificar al ms-notificaciones
        try {
            notificacionClient.enviarNotificacion(new NotificacionDto(
                guardado.getId(),
                "REGISTRO",
                "Bienvenido al festival, " + guardado.getNombre() + "!"
            ));
        } catch (Exception e) {
            log.warn("No se pudo enviar notificacion de registro: {}", e.getMessage());
        }

        // Consultar eventos publicados desde ms-evento para logging y trazabilidad
        try {
            List<EventoDto> eventosDisponibles = eventoClient.obtenerEventosPublicados();
            log.info("Usuario {} registrado. Eventos publicados disponibles: {}",
                guardado.getEmail(), eventosDisponibles.size());
        } catch (Exception e) {
            log.warn("No se pudo consultar eventos disponibles: {}", e.getMessage());
        }

        return usuarioMapper.toDTO(guardado);
    }

    public UsuarioDto actualizar(Long id, UsuarioDto dto) {
        log.info("Actualizando usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + dto.getRolId()));

        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(rol);

        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Usuario actualizado: {}", actualizado.getId());
        return usuarioMapper.toDTO(actualizado);
    }

    public void desactivar(Long id) {
        log.info("Desactivando usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuario desactivado: {}", id);

        // Notificar al ms-notificaciones
        try {
            notificacionClient.enviarNotificacion(new NotificacionDto(
                id,
                "DESACTIVACION",
                "Tu cuenta ha sido desactivada."
            ));
        } catch (Exception e) {
            log.warn("No se pudo enviar notificacion de desactivacion: {}", e.getMessage());
        }
    }

    /**
     * Verifica si un evento está publicado consultando ms-evento.
     * Útil para validar disponibilidad antes de operaciones relacionadas.
     */
    public boolean verificarEventoDisponible(Long eventoId) {
        log.info("Verificando disponibilidad del evento: {}", eventoId);
        boolean disponible = eventoClient.isEventoPublicado(eventoId);
        log.info("Evento {}: disponible={}", eventoId, disponible);
        return disponible;
    }

    /**
     * Retorna los eventos publicados disponibles desde ms-evento.
     */
    public List<EventoDto> obtenerEventosDisponibles() {
        log.info("Consultando eventos disponibles desde ms-evento");
        return eventoClient.obtenerEventosPublicados();
    }
}
