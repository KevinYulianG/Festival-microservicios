package com.festival.ms_notificaciones.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.festival.ms_notificaciones.dto.NotificacionDTO;
import com.festival.ms_notificaciones.dto.NotificacionRequestDTO;
import com.festival.ms_notificaciones.exception.ResourceNotFoundException;
import com.festival.ms_notificaciones.mapper.NotificacionMapper;
import com.festival.ms_notificaciones.model.Notificacion;
import com.festival.ms_notificaciones.repository.NotificacionRepository;
import com.festival.ms_notificaciones.util.NotificacionTestDataFactory;

@ExtendWith(MockitoExtension.class)
class NotificacionesServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private NotificacionMapper notificacionMapper;

    @InjectMocks
    private NotificacionesService notificacionesService;

    @Test
    @DisplayName("Debe guardar una notificación exitosamente")
    void agregarNotificacionExitoso() {
        NotificacionRequestDTO request = NotificacionTestDataFactory.requestFalso();
        Notificacion entidadGuardada = NotificacionTestDataFactory.entidadFalsa();
        NotificacionDTO dtoRespuesta = NotificacionTestDataFactory.dtoFalso();

        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(entidadGuardada);
        when(notificacionMapper.toDTO(entidadGuardada)).thenReturn(dtoRespuesta);

        NotificacionDTO resultado = notificacionesService.agregarNotificacion(request);

        assertNotNull(resultado);
        assertEquals(dtoRespuesta.getId(), resultado.getId());
        assertEquals(dtoRespuesta.getMensaje(), resultado.getMensaje());
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Debe listar notificaciones por tipo correctamente")
    void listaNotificacionesPorTipoExitoso() {
        String tipo = "ALERTA";
        Notificacion entidad = NotificacionTestDataFactory.entidadFalsa();
        NotificacionDTO dto = NotificacionTestDataFactory.dtoFalso();

        when(notificacionRepository.findByTipo(tipo)).thenReturn(List.of(entidad));
        when(notificacionMapper.toDTO(entidad)).thenReturn(dto);

        List<NotificacionDTO> resultado = notificacionesService.listaNotificacionesPorTipo(tipo);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(tipo, resultado.get(0).getTipo());
        verify(notificacionRepository).findByTipo(tipo);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si no hay notificaciones por tipo")
    void listaNotificacionesPorTipoLanzaException() {
        String tipo = "SISTEMA";
        when(notificacionRepository.findByTipo(tipo)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> 
            notificacionesService.listaNotificacionesPorTipo(tipo)
        );
        verify(notificacionRepository).findByTipo(tipo);
    }

    @Test
    @DisplayName("Debe listar notificaciones por usuario correctamente")
    void listaNotificacionesPorUsuarioExitoso() {
        Long usuarioId = 1L;
        Notificacion entidad = NotificacionTestDataFactory.entidadFalsa();
        NotificacionDTO dto = NotificacionTestDataFactory.dtoFalso();

        when(notificacionRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(entidad));
        when(notificacionMapper.toDTO(entidad)).thenReturn(dto);

        List<NotificacionDTO> resultado = notificacionesService.listaNotificacionesPorUsuario(usuarioId);

        assertFalse(resultado.isEmpty());
        assertEquals(usuarioId, resultado.get(0).getUsuarioId());
        verify(notificacionRepository).findByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el usuario no tiene notificaciones")
    void listaNotificacionesPorUsuarioLanzaException() {
        Long usuarioId = 999L;
        when(notificacionRepository.findByUsuarioId(usuarioId)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> 
            notificacionesService.listaNotificacionesPorUsuario(usuarioId)
        );
        verify(notificacionRepository).findByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Debe eliminar una notificación si existe")
    void eliminarNotificacionesExitoso() {
        Long id = 100L;
        Notificacion entidad = NotificacionTestDataFactory.entidadFalsa();
        when(notificacionRepository.findById(id)).thenReturn(Optional.of(entidad));
        doNothing().when(notificacionRepository).deleteById(id);

        notificacionesService.eliminarNotificaciones(id);

        verify(notificacionRepository).findById(id);
        verify(notificacionRepository).deleteById(id);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException al intentar eliminar una notificación inexistente")
    void eliminarNotificacionesLanzaException() {
        Long id = 100L;
        when(notificacionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            notificacionesService.eliminarNotificaciones(id)
        );
        verify(notificacionRepository).findById(id);
        verify(notificacionRepository, never()).deleteById(anyLong());
    }
}