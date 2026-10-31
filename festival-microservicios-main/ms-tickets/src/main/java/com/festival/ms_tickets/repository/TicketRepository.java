package com.festival.ms_tickets.repository;

import com.festival.ms_tickets.model.TicketEstado;
import com.festival.ms_tickets.model.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Tickets, Long> {

    List<Tickets> encontrasrPorUsuario(Long usuarioId);

    List<Tickets> encontrarPorId(Long eventoId);

    Optional<Tickets> encontrarCodigoQr(String codigoQr);

    long contarEventoyEstado(Long eventoId, List<TicketEstado> estados);

    boolean existeEventoUsuarioEntrada(
        Long usuarioId,
        Long eventoId,
        String tipoEntrada
    );
}