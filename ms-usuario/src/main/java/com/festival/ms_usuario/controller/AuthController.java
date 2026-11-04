package com.festival.ms_usuario.controller;

import com.festival.ms_usuario.dto.LoginDto;
import com.festival.ms_usuario.dto.TokenResponseDto;
import com.festival.ms_usuario.dto.UsuarioDto;
import com.festival.ms_usuario.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "Login y registro de usuarios")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica al usuario y retorna un token JWT para usar en los demás endpoints"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso, retorna token JWT"),
        @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "401", description = "Email o contraseña incorrectos")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginDto dto) {
        log.info("POST /api/auth/login - email: {}", dto.getEmail());
        return ResponseEntity.ok(authService.login(dto));
    }

    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una nueva cuenta de usuario en el sistema"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado")
    })
    @PostMapping("/registro")
    public ResponseEntity<UsuarioDto> registro(@Valid @RequestBody UsuarioDto dto) {
        log.info("POST /api/auth/registro - email: {}", dto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registro(dto));
    }
}
