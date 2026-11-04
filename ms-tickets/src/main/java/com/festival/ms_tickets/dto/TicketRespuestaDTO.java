package com.festival.ms_tickets.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.festival.ms_tickets.model.TicketEstado;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRespuestaDTO {

    private Long id;
    private Long usuarioId;
    private Long eventoId;
    private String tipoEntrada;
    private String codigoQr;
    private TicketEstado estado;
    private BigDecimal precioPagado;
    private LocalDateTime fechaCompra;
    private LocalDateTime fechaValidacion; // null si aún no se ha validado
}