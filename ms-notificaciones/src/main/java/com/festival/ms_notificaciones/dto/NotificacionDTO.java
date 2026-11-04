package com.festival.ms_notificaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionDTO {
    
    private Long id;

    @NotBlank(message = "Es obligatorio  ingresar el tipo de notificacion")
    private String tipo;
    @NotBlank(message = "Es obligatorio ingresar un mensaje de notificacion")
    private String mensaje;
    @NotNull(message = "Es obligatorio ingresar el id del usuario")
    private Long usuarioId;
}
