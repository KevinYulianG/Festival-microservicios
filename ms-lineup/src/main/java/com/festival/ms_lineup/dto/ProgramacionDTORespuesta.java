package com.festival.ms_lineup.dto;

import com.festival.ms_lineup.model.EstadoProgramacion;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ProgramacionDTORespuesta {

    private Long id;
    private Long artistaId;
    private String nombreArtista;
    private Long eventoId;
    private String nombreEscenario;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private EstadoProgramacion estado;
}