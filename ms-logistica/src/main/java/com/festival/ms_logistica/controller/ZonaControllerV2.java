package com.festival.ms_logistica.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.festival.ms_logistica.assembler.ZonaAssembler;
import com.festival.ms_logistica.dto.ZonaDTO;
import com.festival.ms_logistica.dto.ZonaRequestDTO;
import com.festival.ms_logistica.service.ZonaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Zonas V2", description = "Gestión de zonas del festival con HATEOAS")
@RestController
@RequestMapping("/api/v2/zonas")
@RequiredArgsConstructor
public class ZonaControllerV2 {

    private final ZonaService zonaService;
    private final ZonaAssembler assembler;

    @Operation(summary = "Crear una nueva zona")
    @PostMapping
    public ResponseEntity<EntityModel<ZonaDTO>> crearZona(
            @Valid @RequestBody ZonaRequestDTO dto) {
        // Llama a la sobrecarga del servicio pasándole el RequestDTO ligero
        ZonaDTO zona = zonaService.crearZona(dto);
        return new ResponseEntity<>(assembler.toModel(zona), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar una zona")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ZonaDTO>> actualizarZona(
            @PathVariable Long id, @Valid @RequestBody ZonaDTO dto) {
        ZonaDTO zona = zonaService.actualizarZona(id, dto);
        return ResponseEntity.ok(assembler.toModel(zona));
    }

    @Operation(summary = "Listar zonas por evento")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<CollectionModel<EntityModel<ZonaDTO>>> listarZonasPorEvento(
            @PathVariable Long eventoId) {
        List<EntityModel<ZonaDTO>> zonas = zonaService
                .listaZonasPorEvento(eventoId)
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(zonas,
                linkTo(methodOn(ZonaControllerV2.class).listarZonasPorEvento(eventoId)).withSelfRel(),
                linkTo(methodOn(ZonaControllerV2.class).crearZona(new ZonaRequestDTO())).withRel("crearZona")));
    }

    @Operation(summary = "Listar zonas sin staff asignado por evento")
    @GetMapping("/evento/{eventoId}/sinstaff")
    public ResponseEntity<CollectionModel<EntityModel<ZonaDTO>>> listarZonasSinStaff(
            @PathVariable Long eventoId) {
        List<EntityModel<ZonaDTO>> zonas = zonaService
                .listaZonasSinStaff(eventoId)
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(zonas,
                linkTo(methodOn(ZonaControllerV2.class)
                        .listarZonasSinStaff(eventoId)).withSelfRel()));
    }

    @Operation(summary = "Eliminar una zona")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarZona(@PathVariable Long id) {
        zonaService.eliminarZona(id);
        return ResponseEntity.noContent().build();
    }
}