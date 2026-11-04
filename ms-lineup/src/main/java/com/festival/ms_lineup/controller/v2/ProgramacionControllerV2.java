package com.festival.ms_lineup.controller.v2;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.festival.ms_lineup.assembler.ProgramacionAssembler;
import com.festival.ms_lineup.dto.ProgramacionDTORespuesta;
import com.festival.ms_lineup.dto.ProgramacionPedidoDTO;
import com.festival.ms_lineup.service.ProgramacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Programaciones V2", description = "Gestión de programaciones del festival con HATEOAS")
@RestController
@RequestMapping("/api/v2/programaciones")
@RequiredArgsConstructor
public class ProgramacionControllerV2 {

    private final ProgramacionService programacionService;
    private final ProgramacionAssembler assembler;

    @Operation(summary = "Programar un artista en un escenario")
    @PostMapping
    public ResponseEntity<EntityModel<ProgramacionDTORespuesta>> programarArtista(
            @Valid @RequestBody ProgramacionPedidoDTO dto) {
        ProgramacionDTORespuesta programacion = programacionService.programarArtista(dto);
        return new ResponseEntity<>(assembler.toModel(programacion), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener una programación por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProgramacionDTORespuesta>> obtenerProgramacion(
            @PathVariable Long id) {
        ProgramacionDTORespuesta programacion = programacionService.obtenerProgramacion(id);
        return ResponseEntity.ok(assembler.toModel(programacion));
    }

    @Operation(summary = "Listar programaciones por evento")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<CollectionModel<EntityModel<ProgramacionDTORespuesta>>> obtenerPorEvento(
            @PathVariable Long eventoId) {
        List<EntityModel<ProgramacionDTORespuesta>> programaciones = programacionService
            .obtenerPorEvento(eventoId)
            .stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(programaciones,
            linkTo(methodOn(ProgramacionControllerV2.class)
                .obtenerPorEvento(eventoId)).withSelfRel()
        ));
    }

    @Operation(summary = "Listar programaciones por artista")
    @GetMapping("/artista/{artistaId}")
    public ResponseEntity<CollectionModel<EntityModel<ProgramacionDTORespuesta>>> obtenerPorArtista(
            @PathVariable Long artistaId) {
        List<EntityModel<ProgramacionDTORespuesta>> programaciones = programacionService
            .obtenerPorArtista(artistaId)
            .stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(programaciones,
            linkTo(methodOn(ProgramacionControllerV2.class)
                .obtenerPorArtista(artistaId)).withSelfRel()
        ));
    }

    @Operation(summary = "Actualizar el horario de una programación")
    @PutMapping("/horario/{id}")
    public ResponseEntity<EntityModel<ProgramacionDTORespuesta>> actualizarHorario(
            @PathVariable Long id, @Valid @RequestBody ProgramacionPedidoDTO dto) {
        ProgramacionDTORespuesta programacion = programacionService.actualizarHorario(id, dto);
        return ResponseEntity.ok(assembler.toModel(programacion));
    }

    @Operation(summary = "Cancelar una programación")
    @PutMapping("/cancelar/{id}")
    public ResponseEntity<EntityModel<ProgramacionDTORespuesta>> cancelarProgramacion(
            @PathVariable Long id) {
        ProgramacionDTORespuesta programacion = programacionService.cancelarProgramacion(id);
        return ResponseEntity.ok(assembler.toModel(programacion));
    }
}