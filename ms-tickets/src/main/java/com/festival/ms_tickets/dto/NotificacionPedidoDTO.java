package com.festival.ms_tickets.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificacionPedidoDTO {

    private String tipo;
    private String mensaje;
    private Long usuarioId;
}