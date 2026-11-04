package com.festival.ms_lineup.controller;

import com.festival.ms_lineup.dto.ArtistaDTORespuesta;
import com.festival.ms_lineup.dto.ArtistaPedidoDTO;
import com.festival.ms_lineup.service.ArtistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/artistas")
@RequiredArgsConstructor
public class ArtistaController {

    private final ArtistaService artistaService;


    @PostMapping
    public ResponseEntity<ArtistaDTORespuesta> crearArtista(
            @Valid @RequestBody ArtistaPedidoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(artistaService.crearArtista(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistaDTORespuesta> obtenerArtista(
            @PathVariable Long id) {
        return ResponseEntity.ok(artistaService.obtenerArtista(id));
    }

    @GetMapping
    public ResponseEntity<List<ArtistaDTORespuesta>> listarArtistas() {
        return ResponseEntity.ok(artistaService.listarArtistas());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistaDTORespuesta> actualizarArtista(
            @PathVariable Long id,
            @Valid @RequestBody ArtistaPedidoDTO dto) {
        return ResponseEntity.ok(artistaService.actualizarArtista(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ArtistaDTORespuesta> desactivarArtista(
            @PathVariable Long id) {
        return ResponseEntity.ok(artistaService.desactivarArtista(id));
    }
}