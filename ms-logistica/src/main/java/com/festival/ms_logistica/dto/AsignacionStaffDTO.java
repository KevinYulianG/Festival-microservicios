package com.festival.ms_logistica.dto;



import com.festival.ms_logistica.model.Zona;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionStaffDTO {

    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    private String usuarioNombre; 

    @NotNull(message = "La zona es obligatoria")
    private Long zonaId;

    @NotNull(message = "La labor es obligatoria")
    private String labor; // vendedor, guardia, etc.

    
    private Zona zona;


}