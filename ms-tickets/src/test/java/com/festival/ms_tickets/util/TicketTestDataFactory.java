package com.festival.ms_tickets.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.festival.ms_tickets.dto.EventoRespuestaDTO;
import com.festival.ms_tickets.dto.TicketPedidoDTO;
import com.festival.ms_tickets.dto.TicketRespuestaDTO;
import com.festival.ms_tickets.dto.UsuarioRespuestaDTO;
import com.festival.ms_tickets.model.TicketEstado;
import com.festival.ms_tickets.model.Tickets;

public class TicketTestDataFactory {

    // DTOs de entrada (request)

    public static TicketPedidoDTO ticketPedidoGeneral() {
        TicketPedidoDTO dto = new TicketPedidoDTO();
        dto.setUsuarioId(4L);
        dto.setEventoId(1L);
        dto.setTipoEntrada("GENERAL");
        dto.setPrecioPagado(new BigDecimal("15000"));
        return dto;
    }

    public static TicketPedidoDTO ticketPedidoVip() {
        TicketPedidoDTO dto = new TicketPedidoDTO();
        dto.setUsuarioId(4L);
        dto.setEventoId(1L);
        dto.setTipoEntrada("VIP");
        dto.setPrecioPagado(new BigDecimal("35000"));
        return dto;
    }

    // Entidades JPA (modelo)

    public static Tickets ticketEntidadComprado() {
        return Tickets.builder()
            .id(1L)
            .usuarioId(4L)
            .eventoId(1L)
            .tipoEntrada("GENERAL")
            .codigoQr("QR-FAKE-001")
            .estado(TicketEstado.COMPRADO)
            .precioPagado(new BigDecimal("15000"))
            .fechaCompra(LocalDateTime.now())
            .fechaValidacion(null)
            .build();
    }

    public static Tickets ticketEntidadUsado() {
        return Tickets.builder()
            .id(2L)
            .usuarioId(4L)
            .eventoId(1L)
            .tipoEntrada("VIP")
            .codigoQr("QR-FAKE-002")
            .estado(TicketEstado.USADO)
            .precioPagado(new BigDecimal("35000"))
            .fechaCompra(LocalDateTime.now().minusHours(2))
            .fechaValidacion(LocalDateTime.now())
            .build();
    }

    public static Tickets ticketEntidadCancelado() {
        return Tickets.builder()
            .id(3L)
            .usuarioId(4L)
            .eventoId(1L)
            .tipoEntrada("BACKSTAGE")
            .codigoQr("QR-FAKE-003")
            .estado(TicketEstado.CANCELADO)
            .precioPagado(new BigDecimal("75000"))
            .fechaCompra(LocalDateTime.now().minusDays(1))
            .fechaValidacion(null)
            .build();
    }

    // DTOs de respuesta (response)

    public static TicketRespuestaDTO ticketRespuestaComprado() {
        TicketRespuestaDTO dto = new TicketRespuestaDTO();
        dto.setId(1L);
        dto.setUsuarioId(4L);
        dto.setEventoId(1L);
        dto.setTipoEntrada("GENERAL");
        dto.setCodigoQr("QR-FAKE-001");
        dto.setEstado(TicketEstado.COMPRADO);
        dto.setPrecioPagado(new BigDecimal("15000"));
        dto.setFechaCompra(LocalDateTime.now());
        dto.setFechaValidacion(null);
        return dto;
    }

    public static TicketRespuestaDTO ticketRespuestaUsado() {
        TicketRespuestaDTO dto = new TicketRespuestaDTO();
        dto.setId(2L);
        dto.setUsuarioId(4L);
        dto.setEventoId(1L);
        dto.setTipoEntrada("VIP");
        dto.setCodigoQr("QR-FAKE-002");
        dto.setEstado(TicketEstado.USADO);
        dto.setPrecioPagado(new BigDecimal("35000"));
        dto.setFechaCompra(LocalDateTime.now().minusHours(2));
        dto.setFechaValidacion(LocalDateTime.now());
        return dto;
    }

    public static TicketRespuestaDTO ticketRespuestaCancelado() {
        TicketRespuestaDTO dto = new TicketRespuestaDTO();
        dto.setId(3L);
        dto.setUsuarioId(4L);
        dto.setEventoId(1L);
        dto.setTipoEntrada("BACKSTAGE");
        dto.setCodigoQr("QR-FAKE-003");
        dto.setEstado(TicketEstado.CANCELADO);
        dto.setPrecioPagado(new BigDecimal("75000"));
        dto.setFechaCompra(LocalDateTime.now().minusDays(1));
        dto.setFechaValidacion(null);
        return dto;
    }

    // DTOs de clientes externos (Feign)

    public static UsuarioRespuestaDTO usuarioActivoFalso() {
        UsuarioRespuestaDTO dto = new UsuarioRespuestaDTO();
        dto.setId(4L);
        dto.setNombre("Ana Asistente");
        dto.setEmail("ana@festival.com");
        dto.setActivo(true);
        dto.setNombreRol("ASISTENTE");
        return dto;
    }

    public static UsuarioRespuestaDTO usuarioInactivoFalso() {
        UsuarioRespuestaDTO dto = new UsuarioRespuestaDTO();
        dto.setId(99L);
        dto.setNombre("Usuario Inactivo");
        dto.setEmail("inactivo@festival.com");
        dto.setActivo(false);
        dto.setNombreRol("ASISTENTE");
        return dto;
    }

    public static EventoRespuestaDTO eventoPublicadoFalso() {
        EventoRespuestaDTO dto = new EventoRespuestaDTO();
        dto.setId(1L);
        dto.setNombre("Festival Reggae 2025");
        dto.setEstado("PUBLICADO");
        dto.setAforoMaximo(3000);
        return dto;
    }

    public static EventoRespuestaDTO eventoNoPuplicadoFalso() {
        EventoRespuestaDTO dto = new EventoRespuestaDTO();
        dto.setId(2L);
        dto.setNombre("Festival Metal 2025");
        dto.setEstado("BORRADOR");
        dto.setAforoMaximo(6000);
        return dto;
    }

    public static EventoRespuestaDTO eventoCanceladoFalso() {
        EventoRespuestaDTO dto = new EventoRespuestaDTO();
        dto.setId(3L);
        dto.setNombre("Festival Cumbia 2025");
        dto.setEstado("CANCELADO");
        dto.setAforoMaximo(4000);
        return dto;
    }
}