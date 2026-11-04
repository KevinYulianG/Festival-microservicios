package com.festival.ms_evento.repository;

import com.festival.ms_evento.model.Evento;
import com.festival.ms_evento.model.EstadoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByEstado(EstadoEvento estado);

    List<Evento> findByEstadoOrderByFechaInicioAsc(EstadoEvento estado);

    List<Evento> findByUbicacionContainingIgnoreCase(String ubicacion);

    @Query("SELECT e FROM Evento e WHERE e.estado = :estado AND e.fechaFin >= :hoy")
    List<Evento> findEventosActivosDesde(@Param("estado") EstadoEvento estado,
                                         @Param("hoy") LocalDate hoy);

    boolean existsByIdAndEstado(Long id, EstadoEvento estado);
}