package com.festival.ms_logistica.mapper;

import org.springframework.stereotype.Component;

import com.festival.ms_logistica.model.Zona;
import com.festival.ms_logistica.dto.ZonaDTO;
@Component
public class ZonaMapper {
    
    public Zona toEntity(ZonaDTO dto){

        Zona zona = new Zona();

        zona.setNombre(dto.getNombre());
        zona.setTipo(dto.getTipo());
        zona.setEventoId(dto.getEventoId());

        return zona;
    }

    public ZonaDTO toDTO(Zona zona){
        
        ZonaDTO dto = new ZonaDTO();

        dto.setId(zona.getId());
        dto.setEventoNombre(zona.getEventoNombre());
        dto.setNombre(zona.getNombre());
        dto.setTipo(zona.getTipo());
        dto.setEventoId(zona.getEventoId());

        return dto;
    }   
    
}
