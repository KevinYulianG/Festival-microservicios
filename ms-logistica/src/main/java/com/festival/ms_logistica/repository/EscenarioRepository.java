package com.festival.ms_logistica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.festival.ms_logistica.model.Escenario;


public interface EscenarioRepository extends JpaRepository<Escenario, Long> {

    List<Escenario> findByEventoId(Long eventoId);
}
