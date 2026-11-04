package com.festival.ms_notificaciones.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.festival.ms_notificaciones.assembler.NotificacionAssembler;
import com.festival.ms_notificaciones.dto.NotificacionDTO;
import com.festival.ms_notificaciones.dto.NotificacionRequestDTO;
import com.festival.ms_notificaciones.service.NotificacionesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notificaciones V2", description = "Notificaciones del negocio con HATEOAS")
@RestController
@RequestMapping("/api/v2/notificaciones")
@RequiredArgsConstructor
public class NotificacionesControllerV2 {

    private final NotificacionesService notificacionesService;
    private final NotificacionAssembler assembler;

    @Operation(summary = "Agregar una notificacion")
    @PostMapping
    public ResponseEntity<EntityModel<NotificacionDTO>> agregarNotificacion(
            @Valid @RequestBody NotificacionRequestDTO dto) {
        NotificacionDTO nueva = notificacionesService.agregarNotificacion(dto);
        return new ResponseEntity<>(assembler.toModel(nueva), HttpStatus.CREATED);
    }

    @Operation(summary = "Lista por el tipo de notificaciones")
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<CollectionModel<EntityModel<NotificacionDTO>>> listarPorTipo(
            @PathVariable String tipo) {
        List<EntityModel<NotificacionDTO>> listaNotis = notificacionesService
            .listaNotificacionesPorTipo(tipo)
            .stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(listaNotis,
            linkTo(methodOn(NotificacionesControllerV2.class)
                .listarPorTipo(tipo)).withSelfRel()));
    }

    @Operation(summary = "Lista por notificaciones de cada usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<NotificacionDTO>>> listarPorUsuario(
            @PathVariable Long usuarioId) {
        List<EntityModel<NotificacionDTO>> listaNotis2 = notificacionesService
            .listaNotificacionesPorUsuario(usuarioId)
            .stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(listaNotis2,
            linkTo(methodOn(NotificacionesControllerV2.class)
                .listarPorUsuario(usuarioId)).withSelfRel()));
    }

    @Operation(summary = "Eliminar notificacion")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionesService.eliminarNotificaciones(id);
        return ResponseEntity.noContent().build();
    }
}