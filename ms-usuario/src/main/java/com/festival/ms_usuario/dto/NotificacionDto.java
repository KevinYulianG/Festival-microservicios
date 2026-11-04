package com.festival.ms_usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionDto {

    private Long usuarioId;

    private String tipo;

    private String mensaje;
}