package com.festival.ms_tickets.service;

import com.festival.ms_tickets.client.EventoClient;
import com.festival.ms_tickets.client.NotificacionClient;
import com.festival.ms_tickets.client.UsuarioClient;
import com.festival.ms_tickets.dto.EventoRespuestaDTO;
import com.festival.ms_tickets.dto.NotificacionPedidoDTO;
import com.festival.ms_tickets.dto.TicketPedidoDTO;
import com.festival.ms_tickets.dto.TicketRespuestaDTO;
import com.festival.ms_tickets.dto.UsuarioRespuestaDTO;
import com.festival.ms_tickets.exception.AforoAgotadoException;
import com.festival.ms_tickets.exception.EventoNoDisponibleException;
import com.festival.ms_tickets.exception.TicketDuplicadoException;
import com.festival.ms_tickets.exception.TicketNoEncontradoException;
import com.festival.ms_tickets.exception.TicketYaUsadoException;
import com.festival.ms_tickets.mapper.TicketMapper;
import com.festival.ms_tickets.model.TicketEstado;
import com.festival.ms_tickets.model.Tickets;
import com.festival.ms_tickets.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final TicketRepository ticketRepository;
    private final UsuarioClient usuarioClient;
    private final EventoClient eventoClient;
    private final NotificacionClient notificacionClient;
    private final TicketMapper ticketMapper;

    @Override
    public TicketRespuestaDTO comprarTicket(TicketPedidoDTO dto) {

        // Regla 1: verificar usuario existe y está activo
        UsuarioRespuestaDTO usuario = usuarioClient.obtenerUsuario(dto.getUsuarioId());
        if (usuario == null || !usuario.isActivo()) {
            log.warn("Usuario no encontrado o inactivo - ID: {}", dto.getUsuarioId());
            throw new TicketNoEncontradoException("Usuario no encontrado o inactivo");
        }

        // Regla 2: verificar evento publicado
        EventoRespuestaDTO evento = eventoClient.obtenerEvento(dto.getEventoId());
        if (!evento.getEstado().equals("PUBLICADO")) {
            log.warn("Evento no disponible - ID: {} estado: {}",
                dto.getEventoId(), evento.getEstado());
            throw new EventoNoDisponibleException(
                "El evento no está disponible para venta");
        }

        // Regla 3: verificar aforo disponible
        long vendidos = ticketRepository.countByEventoIdAndEstadoIn(
            dto.getEventoId(),
            List.of(TicketEstado.COMPRADO, TicketEstado.USADO)
        );
        if (vendidos >= evento.getAforoMaximo()) {
            log.warn("Aforo agotado - eventoId: {} aforoMaximo: {} vendidos: {}",
                dto.getEventoId(), evento.getAforoMaximo(), vendidos);
            throw new AforoAgotadoException(
                "No hay entradas disponibles para este evento");
        }

        // Regla 4: verificar ticket duplicado
        if (ticketRepository.existsByUsuarioIdAndEventoIdAndTipoEntrada(
                dto.getUsuarioId(), dto.getEventoId(), dto.getTipoEntrada())) {
            log.warn("Ticket duplicado - usuarioId: {} eventoId: {} tipo: {}",
                dto.getUsuarioId(), dto.getEventoId(), dto.getTipoEntrada());
            throw new TicketDuplicadoException(
                "Ya tienes un ticket de tipo " + dto.getTipoEntrada()
                + " para este evento");
        }

        // Crear ticket
        Tickets ticket = ticketRepository.save(
            Tickets.builder()
                .usuarioId(dto.getUsuarioId())
                .eventoId(dto.getEventoId())
                .tipoEntrada(dto.getTipoEntrada())
                .codigoQr(UUID.randomUUID().toString())
                .estado(TicketEstado.COMPRADO)
                .precioPagado(dto.getPrecioPagado())
                .fechaCompra(LocalDateTime.now())
                .fechaValidacion(null)
                .build()
        );

        log.info("Ticket comprado exitosamente - usuarioId: {} eventoId: {} QR: {}",
            dto.getUsuarioId(), dto.getEventoId(), ticket.getCodigoQr());

        // Notificar compra exitosa
        try {
            notificacionClient.enviarNotificacion(
                NotificacionPedidoDTO.builder()
                    .tipo("COMPRA_TICKET")
                    .mensaje("Tu ticket fue comprado exitosamente")
                    .usuarioId(dto.getUsuarioId())
                    .build()
            );
            log.info("Notificación de compra enviada - usuarioId: {}",
                dto.getUsuarioId());
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de compra - usuarioId: {}",
                dto.getUsuarioId());
        }

        return ticketMapper.toDTO(ticket);
    }

    @Override
    public TicketRespuestaDTO obtenerTicket(Long id) {
        Tickets ticket = ticketRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Ticket no encontrado - ID: {}", id);
                return new TicketNoEncontradoException(
                    "Ticket no encontrado con ID: " + id);
            });
        return ticketMapper.toDTO(ticket);
    }

    @Override
    public List<TicketRespuestaDTO> obtenerPorUsuario(Long usuarioId) {
        log.info("Consultando tickets del usuario: {}", usuarioId);
        return ticketMapper.toDTOList(
            ticketRepository.findByUsuarioId(usuarioId));
    }

    @Override
    public List<TicketRespuestaDTO> obtenerPorEvento(Long eventoId) {
        log.info("Consultando tickets del evento: {}", eventoId);
        return ticketMapper.toDTOList(
            ticketRepository.findByEventoId(eventoId));
    }

    @Override
    public TicketRespuestaDTO validarEntrada(String codigoQr) {

        Tickets ticket = ticketRepository.findByCodigoQr(codigoQr)
            .orElseThrow(() -> {
                log.warn("QR no válido: {}", codigoQr);
                return new TicketNoEncontradoException(
                    "Código QR no válido o no existe");
            });

        if (ticket.getEstado() == TicketEstado.USADO) {
            log.warn("Reuso de ticket - QR: {}", codigoQr);
            throw new TicketYaUsadoException("Este ticket ya fue utilizado");
        }

        if (ticket.getEstado() == TicketEstado.CANCELADO) {
            log.warn("Uso de ticket cancelado - QR: {}", codigoQr);
            throw new TicketNoEncontradoException(
                "Este ticket fue cancelado y no es válido");
        }

        ticket.setEstado(TicketEstado.USADO);
        ticket.setFechaValidacion(LocalDateTime.now());
        ticketRepository.save(ticket);

        log.info("Entrada validada exitosamente - QR: {}", codigoQr);
        return ticketMapper.toDTO(ticket);
    }

    @Override
    public TicketRespuestaDTO cancelarTicket(Long id) {

        Tickets ticket = ticketRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Ticket no encontrado para cancelar - ID: {}", id);
                return new TicketNoEncontradoException(
                    "Ticket no encontrado con ID: " + id);
            });

        if (ticket.getEstado() != TicketEstado.COMPRADO) {
            log.warn("Cancelación inválida - ID: {} estado: {}",
                id, ticket.getEstado());
            throw new TicketYaUsadoException(
                "Solo se pueden cancelar tickets en estado COMPRADO. Estado actual: "
                + ticket.getEstado());
        }

        ticket.setEstado(TicketEstado.CANCELADO);
        ticketRepository.save(ticket);
        log.info("Ticket cancelado exitosamente - ID: {}", id);

        // Notificar cancelación
        try {
            notificacionClient.enviarNotificacion(
                NotificacionPedidoDTO.builder()
                    .tipo("CANCELACION_TICKET")
                    .mensaje("Tu ticket ha sido cancelado exitosamente")
                    .usuarioId(ticket.getUsuarioId())
                    .build()
            );
            log.info("Notificación de cancelación enviada - usuarioId: {}",
                ticket.getUsuarioId());
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de cancelación - ID: {}", id);
        }

        return ticketMapper.toDTO(ticket);
    }
}