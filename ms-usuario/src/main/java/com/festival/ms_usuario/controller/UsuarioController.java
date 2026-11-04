package com.festival.ms_usuario.controller;

import com.festival.ms_usuario.assembler.UsuarioAssembler;
import com.festival.ms_usuario.dto.EventoDto;
import com.festival.ms_usuario.dto.UsuarioDto;
import com.festival.ms_usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Usuarios", description = "Gestión de usuarios del festival")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;
    private final UsuarioAssembler assembler;

    @Operation(summary = "Listar usuarios activos")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UsuarioDto>>> listar() {
        log.info("GET /api/usuarios");

        List<EntityModel<UsuarioDto>> usuarios = usuarioService.listarActivos()
            .stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(usuarios,
            linkTo(methodOn(UsuarioController.class).listar()).withSelfRel()
        ));
    }

    @Operation(summary = "Buscar usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioDto>> buscarPorId(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        log.info("GET /api/usuarios/{}", id);
        return ResponseEntity.ok(assembler.toModel(usuarioService.buscarPorId(id)));
    }

    @Operation(summary = "Crear usuario (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o email duplicado")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UsuarioDto>> crear(@Valid @RequestBody UsuarioDto dto) {
        log.info("POST /api/usuarios - email: {}", dto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(assembler.toModel(usuarioService.crear(dto)));
    }

    @Operation(summary = "Actualizar usuario (solo ADMIN)")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioDto>> actualizar(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @Valid @RequestBody UsuarioDto dto) {
        log.info("PUT /api/usuarios/{}", id);
        return ResponseEntity.ok(assembler.toModel(usuarioService.actualizar(id, dto)));
    }

    @Operation(summary = "Desactivar usuario (solo ADMIN)")
    @ApiResponse(responseCode = "204", description = "Usuario desactivado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        log.info("DELETE /api/usuarios/{}", id);
        usuarioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener eventos disponibles desde ms-evento")
    @ApiResponse(responseCode = "200", description = "Lista de eventos publicados")
    @GetMapping("/eventos-disponibles")
    public ResponseEntity<List<EventoDto>> obtenerEventosDisponibles() {
        log.info("GET /api/usuarios/eventos-disponibles");
        return ResponseEntity.ok(usuarioService.obtenerEventosDisponibles());
    }

    @Operation(summary = "Verificar si un evento está disponible")
    @ApiResponse(responseCode = "200", description = "true si disponible, false si no")
    @GetMapping("/eventos/{eventoId}/disponible")
    public ResponseEntity<Boolean> verificarEventoDisponible(
            @Parameter(description = "ID del evento") @PathVariable Long eventoId) {
        log.info("GET /api/usuarios/eventos/{}/disponible", eventoId);
        return ResponseEntity.ok(usuarioService.verificarEventoDisponible(eventoId));
    }
}
