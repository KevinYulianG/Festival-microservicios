package com.festival.ms_notificaciones.mapper;

import org.springframework.stereotype.Component;

import com.festival.ms_notificaciones.dto.NotificacionDTO;
import com.festival.ms_notificaciones.model.Notificacion;
@Component
public class NotificacionMapper {
    
    public Notificacion toEntity(NotificacionDTO dto){
        Notificacion notificacion = new Notificacion();

        notificacion.setTipo(dto.getTipo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setUsuarioId(dto.getUsuarioId());
        
        return notificacion;
    }

    public NotificacionDTO toDTO(Notificacion notificacion){

        NotificacionDTO dto = new NotificacionDTO();

        dto.setId(notificacion.getId());
        dto.setTipo(notificacion.getTipo());
        dto.setMensaje(notificacion.getMensaje());
        dto.setUsuarioId(notificacion.getUsuarioId());

        return dto;
    }
}
