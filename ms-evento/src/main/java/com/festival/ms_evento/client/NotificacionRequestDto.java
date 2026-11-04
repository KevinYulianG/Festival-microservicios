package com.festival.ms_evento.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRequestDto {
    private String destinatario;
    private String asunto;
    private String mensaje;
}
