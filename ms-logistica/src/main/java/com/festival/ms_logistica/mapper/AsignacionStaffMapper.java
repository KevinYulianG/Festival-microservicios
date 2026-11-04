package com.festival.ms_logistica.mapper;

import org.springframework.stereotype.Component;

import com.festival.ms_logistica.dto.AsignacionStaffDTO;
import com.festival.ms_logistica.model.AsignacionStaff;

@Component
public class AsignacionStaffMapper {
 
    public AsignacionStaff toEntity(AsignacionStaffDTO dto){

        AsignacionStaff staff = new AsignacionStaff();

        staff.setUsuarioNombre(dto.getUsuarioNombre());
        staff.setUsuarioId(dto.getUsuarioId());
        staff.setLabor(dto.getLabor());
        
        

        return staff;
    }

    public AsignacionStaffDTO toDTO(AsignacionStaff staff){

        AsignacionStaffDTO dto = new AsignacionStaffDTO();

        dto.setUsuarioNombre(staff.getUsuarioNombre());
        dto.setUsuarioId(staff.getUsuarioId());
        dto.setLabor(staff.getLabor());
        dto.setZonaId(staff.getZona().getId());

        return dto;
    }
}
