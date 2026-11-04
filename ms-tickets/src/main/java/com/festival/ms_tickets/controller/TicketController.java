package com.festival.ms_tickets.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.festival.ms_tickets.dto.TicketPedidoDTO;
import com.festival.ms_tickets.dto.TicketRespuestaDTO;
import com.festival.ms_tickets.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketRespuestaDTO> comprarTicket(
            @Valid @RequestBody TicketPedidoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.comprarTicket(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketRespuestaDTO> obtenerTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.obtenerTicket(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TicketRespuestaDTO>> obtenerPorUsuario(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(ticketService.obtenerPorUsuario(usuarioId));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<TicketRespuestaDTO>> obtenerPorEvento(
            @PathVariable Long eventoId) {
        return ResponseEntity.ok(ticketService.obtenerPorEvento(eventoId));
    }

    @PutMapping("/validar/{codigoQR}")
    public ResponseEntity<TicketRespuestaDTO> validarEntrada(
            @PathVariable String codigoQR) {
        return ResponseEntity.ok(ticketService.validarEntrada(codigoQR));
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<TicketRespuestaDTO> cancelarTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.cancelarTicket(id));
    }
}