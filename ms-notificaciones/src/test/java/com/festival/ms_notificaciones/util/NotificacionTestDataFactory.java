package com.festival.ms_notificaciones.util;

import com.festival.ms_notificaciones.dto.NotificacionDTO;
import com.festival.ms_notificaciones.dto.NotificacionRequestDTO;
import com.festival.ms_notificaciones.model.Notificacion;

import java.time.LocalDateTime;

public class NotificacionTestDataFactory {

    public static NotificacionRequestDTO requestFalso() {
        NotificacionRequestDTO request = new NotificacionRequestDTO();
        request.setTipo("ALERTA");
        request.setMensaje("El concierto está por comenzar");
        request.setUsuarioId(1L);
        return request;
    }

    public static NotificacionDTO dtoFalso() {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(100L);
        dto.setTipo("ALERTA");
        dto.setMensaje("El concierto está por comenzar");
        dto.setUsuarioId(1L);
        return dto;
    }

    public static Notificacion entidadFalsa() {
        Notificacion entidad = new Notificacion();
        entidad.setId(100L);
        entidad.setTipo("ALERTA");
        entidad.setMensaje("El concierto está por comenzar");
        entidad.setUsuarioId(1L);
        entidad.setFechaEnvio(LocalDateTime.now());
        return entidad;
    }
}