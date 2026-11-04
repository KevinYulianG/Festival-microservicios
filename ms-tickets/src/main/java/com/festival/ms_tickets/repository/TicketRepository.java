package com.festival.ms_tickets.repository;

import com.festival.ms_tickets.model.TicketEstado;
import com.festival.ms_tickets.model.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Tickets, Long> {

    List<Tickets> findByUsuarioId(Long usuarioId);

    List<Tickets> findByEventoId(Long eventoId);

    Optional<Tickets> findByCodigoQr(String codigoQr);

    long countByEventoIdAndEstadoIn(Long eventoId, List<TicketEstado> estados);

    boolean existsByUsuarioIdAndEventoIdAndTipoEntrada(
        Long usuarioId,
        Long eventoId,
        String tipoEntrada
    );
}