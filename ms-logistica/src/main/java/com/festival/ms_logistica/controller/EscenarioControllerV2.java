package com.festival.ms_logistica.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.festival.ms_logistica.assembler.EscenarioAssembler;
import com.festival.ms_logistica.dto.EscenarioDTO;
import com.festival.ms_logistica.dto.EscenarioRequestDTO; // <-- IMPORT AGREGADO
import com.festival.ms_logistica.service.EscenarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Escenarios V2", description = "Gestión de escenarios del festival con HATEOAS")
@RestController
@RequestMapping("/api/v2/escenarios")
@RequiredArgsConstructor
public class EscenarioControllerV2 {

    private final EscenarioService escenarioService;
    private final EscenarioAssembler assembler;

    @Operation(summary = "Crear un nuevo escenario")
    @PostMapping
    public ResponseEntity<EntityModel<EscenarioDTO>> crearEscenario(
            @Valid @RequestBody EscenarioRequestDTO dto) { // <-- Ahora se reconoce perfectamente gracias al import
        EscenarioDTO escenario = escenarioService.crearEscenario(dto);
        return new ResponseEntity<>(assembler.toModel(escenario), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un escenario")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<EscenarioDTO>> actualizarEscenario(
            @PathVariable Long id, @Valid @RequestBody EscenarioDTO dto) {
        EscenarioDTO escenario = escenarioService.actualizEscenario(id, dto);
        return ResponseEntity.ok(assembler.toModel(escenario));
    }

    @Operation(summary = "Listar escenarios por evento")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<CollectionModel<EntityModel<EscenarioDTO>>> ListarEscenariosDelEvento(
            @PathVariable Long eventoId) {
        List<EntityModel<EscenarioDTO>> escenarios = escenarioService
            .listaDeEscenariosPorEvento(eventoId)
            .stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(escenarios,
            linkTo(methodOn(EscenarioControllerV2.class)
                .ListarEscenariosDelEvento(eventoId)).withSelfRel(),
            linkTo(methodOn(EscenarioControllerV2.class)
                .crearEscenario(new EscenarioRequestDTO())).withRel("crearEscenario") // <-- CORREGIDO: instanciado para evitar NullPointerException con @Valid
        ));
    }

    @Operation(summary = "Eliminar un escenario")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        escenarioService.eliminarEscenario(id);
        return ResponseEntity.noContent().build();
    }
}