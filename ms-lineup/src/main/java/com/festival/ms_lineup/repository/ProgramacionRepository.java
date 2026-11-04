package com.festival.ms_lineup.repository;

import com.festival.ms_lineup.model.EstadoProgramacion;
import com.festival.ms_lineup.model.ProgramacionArtista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProgramacionRepository extends JpaRepository<ProgramacionArtista, Long> {

    List<ProgramacionArtista> findByEventoId(Long eventoId);

    List<ProgramacionArtista> findByArtistaId(Long artistaId);

    List<ProgramacionArtista> findByNombreEscenarioAndEventoId(
        String nombreEscenario, Long eventoId);

    @Query("SELECT COUNT(p) > 0 FROM ProgramacionArtista p " +
        "WHERE p.nombreEscenario = :escenario " +
        "AND p.eventoId = :eventoId " +
        "AND p.estado != 'CANCELADO' " +
        "AND p.horaInicio < :horaFin " +
        "AND p.horaFin > :horaInicio")
        
    boolean existeConflictoHorario(
        @Param("escenario") String escenario,
        @Param("eventoId") Long eventoId,
        @Param("horaInicio") LocalDateTime horaInicio,
        @Param("horaFin") LocalDateTime horaFin
    );

    List<ProgramacionArtista> findByEstado(EstadoProgramacion estado);
}