package com.festival.ms_logistica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZonaDTO {
    

  private Long id;

  @NotBlank(message = "Es obligatorio ingresar un nombre")  
  private String nombre;
  
  @NotBlank(message = "Es obligatorio ingresar el tipo de zona")
  private String tipo;
  
  @NotNull(message = "El ID del evento es obligatorio")
  private Long eventoId;

  private Long usuarioId;
  private String eventoNombre;
}
