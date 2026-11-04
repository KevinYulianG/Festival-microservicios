package com.festival.ms_logistica.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.festival.ms_logistica.assembler.AsignacionStaffAssembler;
import com.festival.ms_logistica.dto.AsignacionStaffDTO;
import com.festival.ms_logistica.dto.AsignacionStaffRequestDTO;
import com.festival.ms_logistica.service.AsignacionStaffService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Asignacion Staff V2", description = "Gestión de asignación de staff con HATEOAS")
@RestController
@RequestMapping("/api/v2/asignaciones")
@RequiredArgsConstructor
public class AsignacionStaffControllerV2 {

    private final AsignacionStaffService asignacionStaffService;
    private final AsignacionStaffAssembler assembler;

    @Operation(summary = "Asignar staff a una zona")
    @PostMapping("/{usuarioId}")
    public ResponseEntity<EntityModel<AsignacionStaffDTO>> asignacion(
            @PathVariable Long usuarioId,
            @Valid @RequestBody AsignacionStaffRequestDTO dto) { // <-- CAMBIO: era AsignacionStaffDTO, ahora AsignacionStaffRequestDTO
        AsignacionStaffDTO asignacion = asignacionStaffService.asignarStaff(usuarioId, dto);
        return new ResponseEntity<>(assembler.toModel(asignacion), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar asignación de staff")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<AsignacionStaffDTO>> actualizarAsignacion(
            @PathVariable Long id,
            @Valid @RequestBody AsignacionStaffDTO dto) { // <-- SIN CAMBIO: lo dejamos como estaba, no lo tocamos todavía
        AsignacionStaffDTO actualizada = asignacionStaffService.actualizarAsignacion(id, dto);
        return new ResponseEntity<>(assembler.toModel(actualizada), HttpStatus.OK);
    }

    @Operation(summary = "Listar staff por zona")
    @GetMapping("/staffZona/{zonaId}")
    public ResponseEntity<CollectionModel<EntityModel<AsignacionStaffDTO>>> listarStaffPorZona(
            @PathVariable long zonaId) {
        List<EntityModel<AsignacionStaffDTO>> asignaciones = asignacionStaffService
            .ListaStaffPorZona(zonaId)
            .stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(asignaciones,
            linkTo(methodOn(AsignacionStaffControllerV2.class)
                .listarStaffPorZona(zonaId)).withSelfRel()
        ));
    }

    @Operation(summary = "Listar staff por evento")
    @GetMapping("/staffEvento/{eventoId}")
    public ResponseEntity<CollectionModel<EntityModel<AsignacionStaffDTO>>> listarStaffPorEvento(
            @PathVariable long eventoId) {
        List<EntityModel<AsignacionStaffDTO>> asignaciones = asignacionStaffService // <-- CORREGIDO: estaba mal puesto AsignacionStaffRequestDTO, vuelve a ser AsignacionStaffDTO
            .listaStaffPorEvento(eventoId)
            .stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(asignaciones,
            linkTo(methodOn(AsignacionStaffControllerV2.class)
                .listarStaffPorEvento(eventoId)).withSelfRel()
        ));
    }

    @Operation(summary = "Eliminar asignación de staff")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAsignacion(@PathVariable Long id) {
        asignacionStaffService.eliminarAsignacion(id);
        return ResponseEntity.noContent().build();
    }
}