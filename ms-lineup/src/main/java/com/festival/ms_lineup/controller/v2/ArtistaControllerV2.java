package com.festival.ms_lineup.controller.v2;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.festival.ms_lineup.assembler.ArtistaAssembler;
import com.festival.ms_lineup.dto.ArtistaDTORespuesta;
import com.festival.ms_lineup.dto.ArtistaPedidoDTO;
import com.festival.ms_lineup.service.ArtistaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Artistas V2", description = "Gestión de artistas del festival con HATEOAS")
@RestController
@RequestMapping("/api/v2/artistas")
@RequiredArgsConstructor
public class ArtistaControllerV2 {

    private final ArtistaService artistaService;
    private final ArtistaAssembler assembler;

    @Operation(summary = "Crear un nuevo artista")
    @PostMapping
    public ResponseEntity<EntityModel<ArtistaDTORespuesta>> crearArtista(
            @Valid @RequestBody ArtistaPedidoDTO dto) {
        ArtistaDTORespuesta artista = artistaService.crearArtista(dto);
        return new ResponseEntity<>(assembler.toModel(artista), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un artista por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ArtistaDTORespuesta>> obtenerArtista(
            @PathVariable Long id) {
        ArtistaDTORespuesta artista = artistaService.obtenerArtista(id);
        return ResponseEntity.ok(assembler.toModel(artista));
    }

    @Operation(summary = "Listar todos los artistas")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ArtistaDTORespuesta>>> listarArtistas() {
        List<EntityModel<ArtistaDTORespuesta>> artistas = artistaService
            .listarArtistas()
            .stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(artistas,
            linkTo(methodOn(ArtistaControllerV2.class).listarArtistas()).withSelfRel()
        ));
    }

    @Operation(summary = "Actualizar un artista")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ArtistaDTORespuesta>> actualizarArtista(
            @PathVariable Long id, @Valid @RequestBody ArtistaPedidoDTO dto) {
        ArtistaDTORespuesta artista = artistaService.actualizarArtista(id, dto);
        return ResponseEntity.ok(assembler.toModel(artista));
    }

    @Operation(summary = "Desactivar un artista")
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<ArtistaDTORespuesta>> desactivarArtista(
            @PathVariable Long id) {
        ArtistaDTORespuesta artista = artistaService.desactivarArtista(id);
        return ResponseEntity.ok(assembler.toModel(artista));
    }
}