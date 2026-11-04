package com.festival.ms_logistica.util;

import com.festival.ms_logistica.dto.AsignacionStaffDTO;
import com.festival.ms_logistica.dto.UsuarioDTO;
import com.festival.ms_logistica.model.AsignacionStaff;
import com.festival.ms_logistica.model.Zona;

public class AsignacionStaffTestDataFactory {
    public static AsignacionStaffDTO asignacionDtoFalsa() {
        AsignacionStaffDTO dto = new AsignacionStaffDTO();
        dto.setUsuarioId(1L);
        dto.setZonaId(1L);
        dto.setLabor("Seguridad");
        return dto;
    }
 
    public static AsignacionStaff asignacionEntidadFalsa(Zona zona) {
        AsignacionStaff asignacion = new AsignacionStaff();
        asignacion.setId(1L);
        asignacion.setUsuarioId(1L);
        asignacion.setUsuarioNombre("Juan Perez");
        asignacion.setLabor("Seguridad");
        asignacion.setZona(zona);
        return asignacion;
    }
 
    public static UsuarioDTO usuarioStaffFalso() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setId(1L);
        usuario.setNombre("Juan Perez");
        usuario.setRolNombre("STAFF");
        return usuario;
    }
 
    public static UsuarioDTO usuarioNoStaffFalso() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setId(1L);
        usuario.setNombre("Juan Perez");
        usuario.setRolNombre("ADMIN");
        return usuario;
    }
}
