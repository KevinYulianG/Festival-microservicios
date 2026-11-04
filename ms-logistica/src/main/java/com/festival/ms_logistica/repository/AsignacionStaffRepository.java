package com.festival.ms_logistica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.festival.ms_logistica.model.AsignacionStaff;


public interface AsignacionStaffRepository  extends JpaRepository<AsignacionStaff, Long>{

List<AsignacionStaff> findByZonaId(Long zonaId);
List<AsignacionStaff> findByZona_EventoId(Long eventoId);

boolean existsByUsuarioIdAndZona_Id(Long usuarioId, Long zonaId);
}
    
    
