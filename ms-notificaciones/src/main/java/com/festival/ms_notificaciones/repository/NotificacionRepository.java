package com.festival.ms_notificaciones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.festival.ms_notificaciones.model.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioId(Long usuarioId);
    List<Notificacion> findByTipo(String tipo);
    
}
