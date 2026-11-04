package com.festival.ms_evento.controller;

import com.festival.ms_evento.DTO.EventoDto;
import com.festival.ms_evento.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private static final Logger log = LoggerFactory.getLogger(EventoController.class);
    private final EventoService eventoService;

    // GET /api/eventos
    @GetMapping
    public ResponseEntity<List<EventoDto>> listar() {
        log.info("GET /api/eventos - listando todos los eventos");
        return ResponseEntity.ok(eventoService.listarTodos());
    }

    // GET /api/eventos/publicados
    @GetMapping("/publicados")
    public ResponseEntity<List<EventoDto>> listarPublicados() {
        log.info("GET /api/eventos/publicados - listando eventos publicados");
        return ResponseEntity.ok(eventoService.listarPublicados());
    }
    // GET /api/eventos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EventoDto> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/eventos/{}", id);
        return ResponseEntity.ok(eventoService.buscarPorId(id));
    }

    // POST /api/eventos
    @PostMapping
    public ResponseEntity<EventoDto> crear(@Valid @RequestBody EventoDto dto) {
        log.info("POST /api/eventos - nombre: {}", dto.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.crear(dto));
    }

    // PUT /api/eventos/{id} 
    @PutMapping("/{id}")
    public ResponseEntity<EventoDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EventoDto dto) {
        log.info("PUT /api/eventos/{} - actualizando evento", id);
        return ResponseEntity.ok(eventoService.actualizar(id, dto));
    }

    // PATCH /api/eventos/{id}/estado 
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EventoDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado) {
        log.info("PATCH /api/eventos/{}/estado -> {}", id, nuevoEstado);
        return ResponseEntity.ok(eventoService.cambiarEstado(id, nuevoEstado));
    }

    // DELETE /api/eventos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/eventos/{}", id);
        eventoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/eventos/{id}/publicado 
    @GetMapping("/{id}/publicado")
    public ResponseEntity<Boolean> isPublicado(@PathVariable Long id) {
        log.info("GET /api/eventos/{}/publicado - verificando estado", id);
        return ResponseEntity.ok(eventoService.isEventoPublicado(id));
    }
}