package com.festival.ms_evento.service;

import com.festival.ms_evento.DTO.EventoDto;
import com.festival.ms_evento.exception.EventoNotFoundException;
import com.festival.ms_evento.mapper.EventoMapper;
import com.festival.ms_evento.model.EstadoEvento;
import com.festival.ms_evento.model.Evento;
import com.festival.ms_evento.repository.EventoRepository;
import com.festival.ms_evento.service.EventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private EventoMapper eventoMapper;

    @InjectMocks
    private EventoService eventoService;

    private Evento evento;
    private EventoDto eventoDto;

    @BeforeEach
    void setUp() {
        evento = new Evento();
        evento.setId(1L);
        evento.setNombre("Festival Rock");
        evento.setUbicacion("Santiago");
        evento.setFechaInicio(LocalDate.of(2025, 6, 1));
        evento.setFechaFin(LocalDate.of(2025, 6, 3));
        evento.setAforoMaximo(1000);
        evento.setEstado(EstadoEvento.BORRADOR);

        eventoDto = new EventoDto();
        eventoDto.setNombre("Festival Rock");
        eventoDto.setUbicacion("Santiago");
        eventoDto.setFechaInicio(LocalDate.of(2025, 6, 1));
        eventoDto.setFechaFin(LocalDate.of(2025, 6, 3));
        eventoDto.setAforoMaximo(1000);
    }

    // CREAR

    @Test
    void crear_deberiaGuardarEvento_cuandoDatosValidos() {
        // Given
        when(eventoMapper.toEntity(eventoDto)).thenReturn(evento);
        when(eventoRepository.save(evento)).thenReturn(evento);
        when(eventoMapper.toDTO(evento)).thenReturn(eventoDto);

        // When
        EventoDto resultado = eventoService.crear(eventoDto);

        // Then
        assertNotNull(resultado);
        verify(eventoRepository, times(1)).save(evento);
    }

    @Test
    void crear_deberiaLanzarExcepcion_cuandoFechaFinEsAnterior() {
        // Given
        eventoDto.setFechaFin(LocalDate.of(2025, 5, 1)); // anterior a inicio

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> eventoService.crear(eventoDto));
        assertTrue(ex.getMessage().contains("fecha de fin"));
        verify(eventoRepository, never()).save(any());
    }

    // BUSCAR POR ID

    @Test
    void buscarPorId_deberiaRetornarDto_cuandoExiste() {
        // Given
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(eventoMapper.toDTO(evento)).thenReturn(eventoDto);

        // When
        EventoDto resultado = eventoService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Festival Rock", resultado.getNombre());
    }

    @Test
    void buscarPorId_deberiaLanzarEventoNotFoundException_cuandoNoExiste() {
        // Given
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EventoNotFoundException.class,
                () -> eventoService.buscarPorId(99L));
    }

    // ACTUALIZAR

    @Test
    void actualizar_deberiaActualizar_cuandoEventoEnBorrador() {
        // Given
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(eventoRepository.save(evento)).thenReturn(evento);
        when(eventoMapper.toDTO(evento)).thenReturn(eventoDto);

        // When
        EventoDto resultado = eventoService.actualizar(1L, eventoDto);

        // Then
        assertNotNull(resultado);
        verify(eventoRepository).save(evento);
    }

    @Test
    void actualizar_deberiaLanzarExcepcion_cuandoEventoCancelado() {
        // Given
        evento.setEstado(EstadoEvento.CANCELADO);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> eventoService.actualizar(1L, eventoDto));
        assertTrue(ex.getMessage().contains("CANCELADO"));
    }

    @Test
    void actualizar_deberiaLanzarExcepcion_cuandoEventoFinalizado() {
        // Given
        evento.setEstado(EstadoEvento.FINALIZADO);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> eventoService.actualizar(1L, eventoDto));
    }

    @Test
    void actualizar_deberiaLanzarExcepcion_cuandoFechaFinEsAnterior() {
        // Given
        eventoDto.setFechaFin(LocalDate.of(2025, 5, 1));
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> eventoService.actualizar(1L, eventoDto));
        assertTrue(ex.getMessage().contains("fecha de fin"));
    }

    // CAMBIAR ESTADO
    
    @Test
    void cambiarEstado_deberiaCambiarAPublicado_cuandoEsBorrador() {
        // Given
        Evento eventoActualizado = new Evento();
        eventoActualizado.setEstado(EstadoEvento.PUBLICADO);

        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(eventoRepository.save(any())).thenReturn(eventoActualizado);
        when(eventoMapper.toDTO(eventoActualizado)).thenReturn(eventoDto);

        // When
        eventoService.cambiarEstado(1L, "PUBLICADO");

        // Then
        verify(eventoRepository, times(1)).save(evento);
        assertEquals(EstadoEvento.PUBLICADO, evento.getEstado());
    }

    @Test
    void cambiarEstado_deberiaCambiarACancelado_cuandoEsPublicado() {
        // Given
        evento.setEstado(EstadoEvento.PUBLICADO);
        Evento eventoActualizado = new Evento();
        eventoActualizado.setEstado(EstadoEvento.CANCELADO);

        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(eventoRepository.save(any())).thenReturn(eventoActualizado);
        when(eventoMapper.toDTO(eventoActualizado)).thenReturn(eventoDto);

        // When
        eventoService.cambiarEstado(1L, "CANCELADO");

        // Then
        assertEquals(EstadoEvento.CANCELADO, evento.getEstado());
    }

    @Test
    void cambiarEstado_deberiaLanzarExcepcion_cuandoTransicionInvalida() {
        // Given — CANCELADO no puede volver a PUBLICADO
        evento.setEstado(EstadoEvento.CANCELADO);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> eventoService.cambiarEstado(1L, "PUBLICADO"));
        assertTrue(ex.getMessage().contains("Transicion invalida"));
    }

    @Test
    void cambiarEstado_deberiaLanzarExcepcion_cuandoEstadoEsInvalido() {
        // Given
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> eventoService.cambiarEstado(1L, "ESTADO_INVENTADO"));
        assertTrue(ex.getMessage().contains("Estado invalido"));
    }

    @Test
    void cambiarEstado_deberiaLanzarExcepcion_cuandoFinalizadoIntentaCambiar() {
        // Given
        evento.setEstado(EstadoEvento.FINALIZADO);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> eventoService.cambiarEstado(1L, "BORRADOR"));
    }

    // ELIMINAR

    @Test
    void eliminar_deberiaEliminar_cuandoEventoEnBorrador() {
        // Given
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        // When
        eventoService.eliminar(1L);

        // Then
        verify(eventoRepository, times(1)).delete(evento);
    }

    @Test
    void eliminar_deberiaLanzarExcepcion_cuandoEventoPublicado() {
        // Given
        evento.setEstado(EstadoEvento.PUBLICADO);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> eventoService.eliminar(1L));
        assertTrue(ex.getMessage().contains("BORRADOR"));
    }

    @Test
    void eliminar_deberiaLanzarEventoNotFoundException_cuandoNoExiste() {
        // Given
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EventoNotFoundException.class,
                () -> eventoService.eliminar(99L));
    }

    // IS PUBLICADO

    @Test
    void isEventoPublicado_deberiaRetornarTrue_cuandoPublicado() {
        // Given
        when(eventoRepository.existsByIdAndEstado(1L, EstadoEvento.PUBLICADO))
                .thenReturn(true);

        // When
        boolean resultado = eventoService.isEventoPublicado(1L);

        // Then
        assertTrue(resultado);
    }

    @Test
    void isEventoPublicado_deberiaRetornarFalse_cuandoNoPublicado() {
        // Given
        when(eventoRepository.existsByIdAndEstado(1L, EstadoEvento.PUBLICADO))
                .thenReturn(false);

        // When
        boolean resultado = eventoService.isEventoPublicado(1L);

        // Then
        assertFalse(resultado);
    }
}
