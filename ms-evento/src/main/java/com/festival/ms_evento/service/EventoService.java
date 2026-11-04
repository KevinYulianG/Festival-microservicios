package com.festival.ms_evento.service;

import com.festival.ms_evento.DTO.EventoDto;
import com.festival.ms_evento.exception.EventoNotFoundException;
import com.festival.ms_evento.mapper.EventoMapper;
import com.festival.ms_evento.model.EstadoEvento;
import com.festival.ms_evento.model.Evento;
import com.festival.ms_evento.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoService {

    private static final Logger log = LoggerFactory.getLogger(EventoService.class);

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;

    public List<EventoDto> listarTodos() {
        log.info("Listando todos los eventos");
        return eventoRepository.findAll()
                .stream()
                .map(eventoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<EventoDto> listarPublicados() {
        log.info("Listando eventos publicados vigentes");
        return eventoRepository.findEventosActivosDesde(EstadoEvento.PUBLICADO, LocalDate.now())
                .stream()
                .map(eventoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public EventoDto buscarPorId(Long id) {
        log.info("Buscando evento con ID: {}", id);
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evento no encontrado: {}", id);
                    return new EventoNotFoundException(id);
                });
        return eventoMapper.toDTO(evento);
    }

    public EventoDto crear(EventoDto dto) {
        log.info("Creando evento: {}", dto.getNombre());

        validarFechas(dto);

        Evento evento = eventoMapper.toEntity(dto);
        Evento guardado = eventoRepository.save(evento);
        log.info("Evento creado con ID: {}", guardado.getId());
        return eventoMapper.toDTO(guardado);
    }

    public EventoDto actualizar(Long id, EventoDto dto) {
        log.info("Actualizando evento: {}", id);
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evento no encontrado para actualizar: {}", id);
                    return new EventoNotFoundException(id);
                });

        if (evento.getEstado() == EstadoEvento.CANCELADO ||
            evento.getEstado() == EstadoEvento.FINALIZADO) {
            throw new RuntimeException("No se puede editar un evento " + evento.getEstado());
        }

        // Validación de fechas también en actualización
        validarFechas(dto);

        eventoMapper.updateEntity(evento, dto);
        return eventoMapper.toDTO(eventoRepository.save(evento));
    }

    public EventoDto cambiarEstado(Long id, String nuevoEstado) {
        log.info("Cambiando estado del evento {} a {}", id, nuevoEstado);
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evento no encontrado para cambiar estado: {}", id);
                    return new EventoNotFoundException(id);
                });

        EstadoEvento estadoActual = evento.getEstado();
        EstadoEvento estadoNuevo;

        try {
            estadoNuevo = EstadoEvento.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado invalido: " + nuevoEstado);
        }

        validarTransicion(estadoActual, estadoNuevo);

        evento.setEstado(estadoNuevo);
        Evento actualizado = eventoRepository.save(evento);
        log.info("Estado cambiado exitosamente: {} -> {}", estadoActual, estadoNuevo);
        return eventoMapper.toDTO(actualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando evento con ID: {}", id);
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evento no encontrado para eliminar: {}", id);
                    return new EventoNotFoundException(id);
                });

        if (evento.getEstado() != EstadoEvento.BORRADOR) {
            throw new RuntimeException(
                "Solo se pueden eliminar eventos en estado BORRADOR. Estado actual: "
                + evento.getEstado());
        }

        eventoRepository.delete(evento);
        log.info("Evento eliminado con ID: {}", id);
    }

    public boolean isEventoPublicado(Long id) {
        return eventoRepository.existsByIdAndEstado(id, EstadoEvento.PUBLICADO);
    }

    private void validarFechas(EventoDto dto) {
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new RuntimeException("La fecha de fin no puede ser anterior a la de inicio");
        }
    }

    private void validarTransicion(EstadoEvento actual, EstadoEvento nuevo) {
        boolean valido = switch (actual) {
            case BORRADOR  -> nuevo == EstadoEvento.PUBLICADO || nuevo == EstadoEvento.CANCELADO;
            case PUBLICADO -> nuevo == EstadoEvento.CANCELADO || nuevo == EstadoEvento.FINALIZADO;
            case CANCELADO, FINALIZADO -> false;
        };
        if (!valido) {
            throw new RuntimeException(
                "Transicion invalida: no se puede pasar de " + actual + " a " + nuevo);
        }
    }
}