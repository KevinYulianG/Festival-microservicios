package com.festival.ms_tickets.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.festival.ms_tickets.client.EventoClient;
import com.festival.ms_tickets.client.UsuarioClient;
import com.festival.ms_tickets.dto.EventoRespuestaDTO;
import com.festival.ms_tickets.dto.TicketPedidoDTO;
import com.festival.ms_tickets.dto.TicketRespuestaDTO;
import com.festival.ms_tickets.dto.UsuarioRespuestaDTO;
import com.festival.ms_tickets.exception.AforoAgotadoException;
import com.festival.ms_tickets.exception.EventoNoDisponibleException;
import com.festival.ms_tickets.exception.TicketDuplicadoException;
import com.festival.ms_tickets.exception.TicketNoEncontradoException;
import com.festival.ms_tickets.exception.TicketYaUsadoException;
import com.festival.ms_tickets.model.TicketEstado;
import com.festival.ms_tickets.model.Tickets;
import com.festival.ms_tickets.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final TicketRepository ticketRepository;
    private final UsuarioClient usuarioClient;
    private final EventoClient eventoClient;

    @Override
    public TicketRespuestaDTO comprarTicket(TicketPedidoDTO dto) {

        // Regla 1: verificar que el usuario existe y está activo
        UsuarioRespuestaDTO usuario = usuarioClient.obtenerUsuario(dto.getUsuarioId());
        if (usuario == null || !usuario.isActivo()) {
            log.warn("Usuario no encontrado o inactivo - ID: {}", dto.getUsuarioId());
            throw new TicketNoEncontradoException("Usuario no encontrado o inactivo");
        }

        // Regla 2: verificar que el evento existe y está publicado
        EventoRespuestaDTO evento = eventoClient.obtenerEvento(dto.getEventoId());
        if (!evento.getEstado().equals("PUBLICADO")) {
            log.warn("Evento no disponible para venta - ID: {} estado: {}",
                dto.getEventoId(), evento.getEstado());
            throw new EventoNoDisponibleException("El evento no está disponible para venta");
        }

        // Regla 3: verificar aforo disponible
        long vendidos = ticketRepository.contarEventoyEstado(
            dto.getEventoId(),
            List.of(TicketEstado.COMPRADO, TicketEstado.USADO)
        );
        if (vendidos >= evento.getCapacidad()) {
            log.warn("Aforo agotado - eventoId: {} capacidad: {} vendidos: {}",
                dto.getEventoId(), evento.getCapacidad(), vendidos);
            throw new AforoAgotadoException("No hay entradas disponibles para este evento");
        }

        // Regla 4: verificar que no exista ticket duplicado
        if (ticketRepository.existeEventoUsuarioEntrada(
                dto.getUsuarioId(), dto.getEventoId(), dto.getTipoEntrada())) {
            log.warn("Ticket duplicado - usuarioId: {} eventoId: {} tipo: {}",
                dto.getUsuarioId(), dto.getEventoId(), dto.getTipoEntrada());
            throw new TicketDuplicadoException(
                "Ya tienes un ticket de tipo " + dto.getTipoEntrada() + " para este evento");
        }

        // Crear y guardar el ticket
        Tickets ticket = Tickets.builder()
            .usuarioId(dto.getUsuarioId())
            .eventoId(dto.getEventoId())
            .tipoEntrada(dto.getTipoEntrada())
            .codigoQr(UUID.randomUUID().toString())
            .estado(TicketEstado.COMPRADO)
            .precioPagado(dto.getPrecioPagado())
            .fechaCompra(LocalDateTime.now())
            .fechaValidacion(null)
            .build();

        ticketRepository.save(ticket);
        log.info("Ticket comprado exitosamente - usuarioId: {} eventoId: {} tipo: {} QR: {}",
            dto.getUsuarioId(), dto.getEventoId(), dto.getTipoEntrada(), ticket.getCodigoQr());

        return mapToDTO(ticket);
    }

    @Override
    public TicketRespuestaDTO obtenerTicket(Long id) {
        Tickets ticket = ticketRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Ticket no encontrado - ID: {}", id);
                return new TicketNoEncontradoException("Ticket no encontrado con ID: " + id);
            });
        return mapToDTO(ticket);
    }

    @Override
    public List<TicketRespuestaDTO> obtenerPorUsuario(Long usuarioId) {
        log.info("Consultando tickets del usuario: {}", usuarioId);
        return ticketRepository.encontrarPorId(usuarioId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<TicketRespuestaDTO> obtenerPorEvento(Long eventoId) {
        log.info("Consultando tickets del evento: {}", eventoId);
        return ticketRepository.encontrarPorId(eventoId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public TicketRespuestaDTO validarEntrada(String codigoQr) {

        // Buscar el ticket por QR
        Tickets ticket = ticketRepository.encontrarCodigoQr(codigoQr)
            .orElseThrow(() -> {
                log.warn("QR no válido: {}", codigoQr);
                return new TicketNoEncontradoException("Código QR no válido o no existe");
            });

        // Verificar que no esté ya usado
        if (ticket.getEstado() == TicketEstado.USADO) {
            log.warn("Intento de reuso de ticket - QR: {}", codigoQr);
            throw new TicketYaUsadoException("Este ticket ya fue utilizado");
        }

        // Verificar que no esté cancelado
        if (ticket.getEstado() == TicketEstado.CANCELADO) {
            log.warn("Intento de uso de ticket cancelado - QR: {}", codigoQr);
            throw new TicketNoEncontradoException("Este ticket fue cancelado y no es válido");
        }

        // Marcar como usado y registrar fecha de validación
        ticket.setEstado(TicketEstado.USADO);
        ticket.setFechaValidacion(LocalDateTime.now());
        ticketRepository.save(ticket);

        log.info("Entrada validada exitosamente - QR: {}", codigoQr);
        return mapToDTO(ticket);
    }

    @Override
    public TicketRespuestaDTO cancelarTicket(Long id) {

        Tickets ticket = ticketRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Ticket no encontrado para cancelar - ID: {}", id);
                return new TicketNoEncontradoException("Ticket no encontrado con ID: " + id);
            });

        // Solo se puede cancelar si está en estado COMPRADO
        if (ticket.getEstado() != TicketEstado.COMPRADO) {
            log.warn("Intento de cancelar ticket en estado incorrecto - ID: {} estado: {}",
                id, ticket.getEstado());
            throw new TicketYaUsadoException(
                "Solo se pueden cancelar tickets en estado COMPRADO. Estado actual: "
                + ticket.getEstado());
        }

        ticket.setEstado(TicketEstado.CANCELADO);
        ticketRepository.save(ticket);

        log.info("Ticket cancelado exitosamente - ID: {}", id);
        return mapToDTO(ticket);
    }

    // Método privado que convierte entidad a DTO de respuesta
    private TicketRespuestaDTO mapToDTO(Tickets ticket) {
        return TicketRespuestaDTO.builder()
            .id(ticket.getId())
            .usuarioId(ticket.getUsuarioId())
            .eventoId(ticket.getEventoId())
            .tipoEntrada(ticket.getTipoEntrada())
            .codigoQr(ticket.getCodigoQr())
            .estado(ticket.getEstado())
            .precioPagado(ticket.getPrecioPagado())
            .fechaCompra(ticket.getFechaCompra())
            .fechaValidacion(ticket.getFechaValidacion())
            .build();
    }
}