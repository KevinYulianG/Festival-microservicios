package com.festival.ms_tickets.service;


import java.util.List;
import com.festival.ms_tickets.dto.TicketPedidoDTO;
import com.festival.ms_tickets.dto.TicketRespuestaDTO;

public interface TicketService {

    TicketRespuestaDTO comprarTicket(TicketPedidoDTO dto);

    TicketRespuestaDTO obtenerTicket(Long id);

    List<TicketRespuestaDTO> obtenerPorUsuario(Long usuarioId);

    List<TicketRespuestaDTO> obtenerPorEvento(Long eventoId);

    TicketRespuestaDTO validarEntrada(String codigoQr);

    TicketRespuestaDTO cancelarTicket(Long id);
}