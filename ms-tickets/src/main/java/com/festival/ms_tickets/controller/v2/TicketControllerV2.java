package com.festival.ms_tickets.controller.v2;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.festival.ms_tickets.assembler.TicketAssembler;
import com.festival.ms_tickets.dto.TicketPedidoDTO;
import com.festival.ms_tickets.dto.TicketRespuestaDTO;
import com.festival.ms_tickets.service.TicketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Tickets V2", description = "Gestión de tickets del festival con HATEOAS")
@RestController
@RequestMapping("/api/v2/tickets")
@RequiredArgsConstructor
public class TicketControllerV2 {

    private final TicketService ticketService;
    private final TicketAssembler assembler;

    // POST /api/v2/tickets
    // Comprar un ticket para un evento

    @Operation(summary = "Comprar un ticket para un evento")
    @PostMapping
    public ResponseEntity<EntityModel<TicketRespuestaDTO>> comprarTicket(
            @Valid @RequestBody TicketPedidoDTO dto) {

        TicketRespuestaDTO ticket = ticketService.comprarTicket(dto);
        return new ResponseEntity<>(assembler.toModel(ticket), HttpStatus.CREATED);
    }

    // GET /api/v2/tickets/{id}
    // Obtener un ticket por su ID

    @Operation(summary = "Obtener un ticket por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TicketRespuestaDTO>> obtenerTicket(
            @PathVariable Long id) {

        TicketRespuestaDTO ticket = ticketService.obtenerTicket(id);
        return ResponseEntity.ok(assembler.toModel(ticket));
    }

    // GET /api/v2/tickets/usuario/{usuarioId}
    // Listar todos los tickets de un usuario

    @Operation(summary = "Listar todos los tickets de un usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<TicketRespuestaDTO>>> obtenerPorUsuario(
            @PathVariable Long usuarioId) {

        List<EntityModel<TicketRespuestaDTO>> tickets = ticketService
            .obtenerPorUsuario(usuarioId)
            .stream()
            .map(ticket -> assembler.toModel(ticket))
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(tickets,
            // Self link a la coleccion de tickets del usuario
            linkTo(methodOn(TicketControllerV2.class)
                .obtenerPorUsuario(usuarioId)).withSelfRel(),
            // Link para comprar un nuevo ticket
            linkTo(methodOn(TicketControllerV2.class)
                .comprarTicket(null)).withRel("comprarTicket")
        ));
    }


    // GET /api/v2/tickets/evento/{eventoId}
    // Listar todos los tickets de un evento

    @Operation(summary = "Listar todos los tickets de un evento")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<CollectionModel<EntityModel<TicketRespuestaDTO>>> obtenerPorEvento(
            @PathVariable Long eventoId) {

        List<EntityModel<TicketRespuestaDTO>> tickets = ticketService
            .obtenerPorEvento(eventoId)
            .stream()
            .map(ticket -> assembler.toModel(ticket))
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(tickets,
            // Self link a la coleccion de tickets del evento
            linkTo(methodOn(TicketControllerV2.class)
                .obtenerPorEvento(eventoId)).withSelfRel(),
            // Link para comprar un nuevo ticket para ese evento
            linkTo(methodOn(TicketControllerV2.class)
                .comprarTicket(null)).withRel("comprarTicket")
        ));
    }


    // PUT /api/v2/tickets/validar/{codigoQR}
    // Validar la entrada de un asistente con su QR

    @Operation(summary = "Validar la entrada de un asistente usando el codigo QR")
    @PutMapping("/validar/{codigoQR}")
    public ResponseEntity<EntityModel<TicketRespuestaDTO>> validarEntrada(
            @PathVariable String codigoQR) {

        TicketRespuestaDTO ticket = ticketService.validarEntrada(codigoQR);
        return ResponseEntity.ok(assembler.toModel(ticket));
    }


    // PUT /api/v2/tickets/cancelar/{id}
    // Cancelar un ticket por su ID

    @Operation(summary = "Cancelar un ticket por su ID")
    @PutMapping("/cancelar/{id}")
    public ResponseEntity<EntityModel<TicketRespuestaDTO>> cancelarTicket(
            @PathVariable Long id) {

        TicketRespuestaDTO ticket = ticketService.cancelarTicket(id);
        return ResponseEntity.ok(assembler.toModel(ticket));
    }
}