package com.festival.ms_lineup.service.impl;

import com.festival.ms_lineup.client.EventoClient;
import com.festival.ms_lineup.client.NotificacionClient;
import com.festival.ms_lineup.dto.EventoDTORespuesta;
import com.festival.ms_lineup.dto.NotificacionPedidoDTO;
import com.festival.ms_lineup.dto.ProgramacionDTORespuesta;
import com.festival.ms_lineup.dto.ProgramacionPedidoDTO;
import com.festival.ms_lineup.exception.ArtistaNoEncontrado;
import com.festival.ms_lineup.exception.ConflictoHorario;
import com.festival.ms_lineup.exception.EventoNoDisponible;
import com.festival.ms_lineup.exception.ProgramacionNoEncontrada;
import com.festival.ms_lineup.mapper.LineupMapper;
import com.festival.ms_lineup.model.Artista;
import com.festival.ms_lineup.model.EstadoProgramacion;
import com.festival.ms_lineup.model.ProgramacionArtista;
import com.festival.ms_lineup.repository.ArtistaRepository;
import com.festival.ms_lineup.repository.ProgramacionRepository;
import com.festival.ms_lineup.service.ProgramacionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramacionServiceImpl implements ProgramacionService {

    private static final Logger log = LoggerFactory.getLogger(
        ProgramacionServiceImpl.class);

    private final ProgramacionRepository programacionRepository;
    private final ArtistaRepository artistaRepository;
    private final EventoClient eventoClient;
    private final NotificacionClient notificacionClient;
    private final LineupMapper lineupMapper;

    @Override
    public ProgramacionDTORespuesta programarArtista(ProgramacionPedidoDTO dto) {

        Artista artista = artistaRepository.findById(dto.getArtistaId())
            .orElseThrow(() -> {
                log.warn("Artista no encontrado - ID: {}", dto.getArtistaId());
                return new ArtistaNoEncontrado(
                    "Artista no encontrado con ID: " + dto.getArtistaId());
            });

        EventoDTORespuesta evento = eventoClient.obtenerEvento(dto.getEventoId());
            if (!evento.getEstado().equals("PUBLICADO")) {
                log.warn("Evento no disponible - ID: {}", dto.getEventoId());
                throw new EventoNoDisponible("El evento no está disponible para programar artistas");
        }

        if (!dto.getHoraFin().isAfter(dto.getHoraInicio())) {
            throw new ConflictoHorario(
                "La hora de fin debe ser posterior a la hora de inicio");
        }


        boolean hayConflicto = programacionRepository.existeConflictoHorario(
            dto.getNombreEscenario(),
            dto.getEventoId(),
            dto.getHoraInicio(),
            dto.getHoraFin()
        );
        if (hayConflicto) {
            log.warn("Conflicto de horario - escenario: {} horaInicio: {}",
                dto.getNombreEscenario(), dto.getHoraInicio());
            throw new ConflictoHorario(
                "Ya existe un artista programado en ese escenario en ese horario");
        }

        ProgramacionArtista programacion = ProgramacionArtista.builder()
            .eventoId(dto.getEventoId())
            .nombreEscenario(dto.getNombreEscenario())
            .horaInicio(dto.getHoraInicio())
            .horaFin(dto.getHoraFin())
            .estado(EstadoProgramacion.PROGRAMADO)
            .build();

        programacion.setArtista(artista);

        log.info("Artista programado - artistaId: {} eventoId: {} escenario: {}",
            dto.getArtistaId(), dto.getEventoId(), dto.getNombreEscenario());

        return lineupMapper.toProgramacionDTO(
            programacionRepository.save(programacion));
    }

    @Override
    public ProgramacionDTORespuesta obtenerProgramacion(Long id) {
        ProgramacionArtista prog = programacionRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Programación no encontrada - ID: {}", id);
                return new ProgramacionNoEncontrada(
                    "Programación no encontrada con ID: " + id);
            });
        return lineupMapper.toProgramacionDTO(prog);
    }

    @Override
    public List<ProgramacionDTORespuesta> obtenerPorEvento(Long eventoId) {
        log.info("Consultando lineup del evento: {}", eventoId);
        return lineupMapper.toProgramacionDTOList(
            programacionRepository.findByEventoId(eventoId));
    }

    @Override
    public List<ProgramacionDTORespuesta> obtenerPorArtista(Long artistaId) {
        log.info("Consultando programaciones del artista: {}", artistaId);
        return lineupMapper.toProgramacionDTOList(
            programacionRepository.findByArtistaId(artistaId));
    }

    @Override
    public ProgramacionDTORespuesta cancelarProgramacion(Long id) {
        ProgramacionArtista prog = programacionRepository.findById(id)
            .orElseThrow(() -> new ProgramacionNoEncontrada(
                "Programación no encontrada con ID: " + id));

        if (prog.getEstado() == EstadoProgramacion.FINALIZADO) {
            throw new ConflictoHorario(
                "No se puede cancelar una programación ya finalizada");
        }

        prog.setEstado(EstadoProgramacion.CANCELADO);
        programacionRepository.save(prog);
        log.info("Programación cancelada - ID: {}", id);

        try {
            notificacionClient.enviarNotificacion(
                NotificacionPedidoDTO.builder()
                    .tipo("CANCELACION_PROGRAMACION")
                    .mensaje("La programación del artista "
                        + prog.getArtista().getNombre()
                        + " ha sido cancelada en el escenario "
                        + prog.getNombreEscenario())
                    .usuarioId(null)
                    .build()
            );
            log.info("Notificación de cancelación enviada - programacionId: {}", id);
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de cancelación - ID: {}", id);
        }

        return lineupMapper.toProgramacionDTO(prog);
    }

    @Override
    public ProgramacionDTORespuesta actualizarHorario(Long id,
            ProgramacionPedidoDTO dto) {
        ProgramacionArtista prog = programacionRepository.findById(id)
            .orElseThrow(() -> new ProgramacionNoEncontrada(
                "Programación no encontrada con ID: " + id));

        boolean hayConflicto = programacionRepository.existeConflictoHorario(
            dto.getNombreEscenario(),
            dto.getEventoId(),
            dto.getHoraInicio(),
            dto.getHoraFin()
        );
        if (hayConflicto) {
            throw new ConflictoHorario(
                "El nuevo horario genera conflicto con otro artista en ese escenario");
        }

        prog.setNombreEscenario(dto.getNombreEscenario());
        prog.setHoraInicio(dto.getHoraInicio());
        prog.setHoraFin(dto.getHoraFin());
        programacionRepository.save(prog);
        log.info("Horario actualizado - ID: {}", id);

        try {
            notificacionClient.enviarNotificacion(
                NotificacionPedidoDTO.builder()
                    .tipo("CAMBIO_HORARIO")
                    .mensaje("El horario del artista "
                        + prog.getArtista().getNombre()
                        + " ha sido actualizado en el escenario "
                        + prog.getNombreEscenario())
                    .usuarioId(null)
                    .build()
            );
            log.info("Notificación de cambio horario enviada - programacionId: {}", id);
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de cambio horario - ID: {}", id);
        }

        return lineupMapper.toProgramacionDTO(prog);
    }
}