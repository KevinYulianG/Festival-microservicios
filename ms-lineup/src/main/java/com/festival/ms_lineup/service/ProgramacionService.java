package com.festival.ms_lineup.service;

import java.util.List;
import com.festival.ms_lineup.dto.ProgramacionDTORespuesta;
import com.festival.ms_lineup.dto.ProgramacionPedidoDTO;

public interface ProgramacionService {

    ProgramacionDTORespuesta programarArtista(ProgramacionPedidoDTO dto);

    ProgramacionDTORespuesta obtenerProgramacion(Long id);

    List<ProgramacionDTORespuesta> obtenerPorEvento(Long eventoId);

    List<ProgramacionDTORespuesta> obtenerPorArtista(Long artistaId);

    ProgramacionDTORespuesta cancelarProgramacion(Long id);

    ProgramacionDTORespuesta actualizarHorario(Long id, ProgramacionPedidoDTO dto);

}