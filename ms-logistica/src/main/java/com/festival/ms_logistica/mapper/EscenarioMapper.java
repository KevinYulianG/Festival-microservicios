package com.festival.ms_logistica.mapper;
import org.springframework.stereotype.Component;

import com.festival.ms_logistica.dto.EscenarioDTO;
import com.festival.ms_logistica.model.Escenario;


@Component
public class EscenarioMapper {
    
    public Escenario toEntity(EscenarioDTO dto) {
       
        Escenario escenario = new Escenario();
        escenario.setId(dto.getId());       
        escenario.setNombre(dto.getNombre());
        escenario.setCapacidad(dto.getCapacidad());
        escenario.setEventoId(dto.getEventoId());
        return escenario;
    }

    public EscenarioDTO toDTO(Escenario escenario) {

        EscenarioDTO dto = new EscenarioDTO();
        dto.setId(escenario.getId());       
        dto.setNombre(escenario.getNombre());
        dto.setCapacidad(escenario.getCapacidad());
        dto.setEventoId(escenario.getEventoId());
        return dto;
    }
}
