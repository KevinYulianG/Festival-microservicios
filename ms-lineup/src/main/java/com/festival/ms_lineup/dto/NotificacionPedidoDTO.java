package com.festival.ms_lineup.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificacionPedidoDTO {

    private String tipo;
    private String mensaje;
    private Long usuarioId;
}