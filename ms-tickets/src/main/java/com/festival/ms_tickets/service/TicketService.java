package com.festival.ms_tickets.service;




import java.util.List;

import com.festival.ms_tickets.dto.TicketPedidoDTO;
import com.festival.ms_tickets.dto.TicketRespuestaDTO;

public interface TicketService {

    // Comprar un ticket nuevo
    TicketRespuestaDTO comprarTicket(TicketPedidoDTO dto);

    // Obtener ticket por ID
    TicketRespuestaDTO obtenerTicket(Long id);

    // Listar todos los tickets de un usuario
    List<TicketRespuestaDTO> obtenerPorUsuario(Long usuarioId);

    // Listar todos los tickets de un evento
    List<TicketRespuestaDTO> obtenerPorEvento(Long eventoId);

    // Validar entrada en puerta mediante QR
    TicketRespuestaDTO validarEntrada(String codigoQr);

    // Cancelar un ticket
    TicketRespuestaDTO cancelarTicket(Long id);
}