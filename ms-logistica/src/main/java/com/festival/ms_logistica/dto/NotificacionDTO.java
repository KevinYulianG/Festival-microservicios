package com.festival.ms_logistica.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionDTO {
    

   
    private String tipo; 
    private String mensaje;
    private Long usuarioId;

}
