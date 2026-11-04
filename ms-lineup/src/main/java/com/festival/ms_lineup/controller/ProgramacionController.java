package com.festival.ms_lineup.controller;

import com.festival.ms_lineup.dto.ProgramacionDTORespuesta;
import com.festival.ms_lineup.dto.ProgramacionPedidoDTO;
import com.festival.ms_lineup.service.ProgramacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/programaciones")
@RequiredArgsConstructor
public class ProgramacionController {

    private final ProgramacionService programacionService;

    @PostMapping
    public ResponseEntity<ProgramacionDTORespuesta> programarArtista(
            @Valid @RequestBody ProgramacionPedidoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(programacionService.programarArtista(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramacionDTORespuesta> obtenerProgramacion(
            @PathVariable Long id) {
        return ResponseEntity.ok(programacionService.obtenerProgramacion(id));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<ProgramacionDTORespuesta>> obtenerPorEvento(
            @PathVariable Long eventoId) {
        return ResponseEntity.ok(programacionService.obtenerPorEvento(eventoId));
    }

    @GetMapping("/artista/{artistaId}")
    public ResponseEntity<List<ProgramacionDTORespuesta>> obtenerPorArtista(
            @PathVariable Long artistaId) {
        return ResponseEntity.ok(programacionService.obtenerPorArtista(artistaId));
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<ProgramacionDTORespuesta> cancelarProgramacion(
            @PathVariable Long id) {
        return ResponseEntity.ok(programacionService.cancelarProgramacion(id));
    }

    @PutMapping("/horario/{id}")
    public ResponseEntity<ProgramacionDTORespuesta> actualizarHorario(
            @PathVariable Long id,
            @Valid @RequestBody ProgramacionPedidoDTO dto) {
        return ResponseEntity.ok(programacionService.actualizarHorario(id, dto));
    }
}