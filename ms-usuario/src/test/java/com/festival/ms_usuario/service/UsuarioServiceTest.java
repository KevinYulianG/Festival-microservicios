package com.festival.ms_usuario.service;

import com.festival.ms_usuario.client.NotificacionClient;
import com.festival.ms_usuario.dto.NotificacionDto;
import com.festival.ms_usuario.dto.UsuarioDto;
import com.festival.ms_usuario.mapper.UsuarioMapper;
import com.festival.ms_usuario.model.Rol;
import com.festival.ms_usuario.model.Usuario;
import com.festival.ms_usuario.repository.RolRepository;
import com.festival.ms_usuario.repository.UsuarioRepository;
import com.festival.ms_usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificacionClient notificacionClient;

    @InjectMocks
    private UsuarioService usuarioService;

    private Rol rolAdmin;
    private Usuario usuario;
    private UsuarioDto usuarioDto;

    @BeforeEach
    void setUp() {
        rolAdmin = new Rol(1L, "ADMIN", "Administrador del sistema");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan Perez");
        usuario.setEmail("juan@festival.com");
        usuario.setPassword("hashedPassword");
        usuario.setRol(rolAdmin);
        usuario.setActivo(true);

        usuarioDto = new UsuarioDto();
        usuarioDto.setId(1L);
        usuarioDto.setNombre("Juan Perez");
        usuarioDto.setEmail("juan@festival.com");
        usuarioDto.setPassword("password123");
        usuarioDto.setRolId(1L);
        usuarioDto.setRolNombre("ADMIN");
        usuarioDto.setActivo(true);
    }

    // ─── listarActivos ───────────────────────────────────────────────

    @Test
    void listarActivos_debeRetornarListaDeUsuariosActivos() {
        // Given
        when(usuarioRepository.findByActivoTrue()).thenReturn(List.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDto);

        // When
        List<UsuarioDto> resultado = usuarioService.listarActivos();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("juan@festival.com", resultado.get(0).getEmail());
        verify(usuarioRepository).findByActivoTrue();
    }

    @Test
    void listarActivos_debeRetornarListaVaciaCuandoNoHayUsuarios() {
        // Given
        when(usuarioRepository.findByActivoTrue()).thenReturn(List.of());

        // When
        List<UsuarioDto> resultado = usuarioService.listarActivos();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ─── buscarPorId ─────────────────────────────────────────────────

    @Test
    void buscarPorId_debeRetornarUsuarioCuandoExiste() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDto);

        // When
        UsuarioDto resultado = usuarioService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("juan@festival.com", resultado.getEmail());
    }

    @Test
    void buscarPorId_debeLanzarExcepcionCuandoNoExiste() {
        // Given
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.buscarPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    // ─── crear ───────────────────────────────────────────────────────

    @Test
    void crear_debeGuardarUsuarioCuandoDatosValidos() {
        // Given
        when(usuarioRepository.existsByEmail(usuarioDto.getEmail())).thenReturn(false);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolAdmin));
        when(usuarioMapper.toEntity(usuarioDto)).thenReturn(usuario);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDto);

        // When
        UsuarioDto resultado = usuarioService.crear(usuarioDto);

        // Then
        assertNotNull(resultado);
        assertEquals("juan@festival.com", resultado.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void crear_debeLanzarExcepcionCuandoEmailYaExiste() {
        // Given
        when(usuarioRepository.existsByEmail(usuarioDto.getEmail())).thenReturn(true);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.crear(usuarioDto));

        assertTrue(ex.getMessage().contains("email"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void crear_debeLanzarExcepcionCuandoRolNoExiste() {
        // Given
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(rolRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.crear(usuarioDto));

        assertTrue(ex.getMessage().contains("Rol"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void crear_debeEnviarNotificacionTrasCrearUsuario() {
        // Given
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(rolRepository.findById(anyLong())).thenReturn(Optional.of(rolAdmin));
        when(usuarioMapper.toEntity(any())).thenReturn(usuario);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(usuarioRepository.save(any())).thenReturn(usuario);
        when(usuarioMapper.toDTO(any())).thenReturn(usuarioDto);

        // When
        usuarioService.crear(usuarioDto);

        // Then
        verify(notificacionClient).enviarNotificacion(any(NotificacionDto.class));
    }

    // ─── actualizar ──────────────────────────────────────────────────

    @Test
    void actualizar_debeModificarUsuarioCuandoExiste() {
        // Given
        UsuarioDto dtoActualizado = new UsuarioDto();
        dtoActualizado.setNombre("Juan Actualizado");
        dtoActualizado.setEmail("juannuevo@festival.com");
        dtoActualizado.setRolId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolAdmin));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDto);

        // When
        UsuarioDto resultado = usuarioService.actualizar(1L, dtoActualizado);

        // Then
        assertNotNull(resultado);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void actualizar_debeLanzarExcepcionCuandoUsuarioNoExiste() {
        // Given
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class,
                () -> usuarioService.actualizar(99L, usuarioDto));
    }

    // ─── desactivar ──────────────────────────────────────────────────

    @Test
    void desactivar_debeCambiarActivoAFalse() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        usuarioService.desactivar(1L);

        // Then
        assertFalse(usuario.getActivo());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void desactivar_debeLanzarExcepcionCuandoUsuarioNoExiste() {
        // Given
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class,
                () -> usuarioService.desactivar(99L));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void desactivar_debeEnviarNotificacionTrasDesactivar() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any())).thenReturn(usuario);

        // When
        usuarioService.desactivar(1L);

        // Then
        verify(notificacionClient).enviarNotificacion(any(NotificacionDto.class));
    }
}
