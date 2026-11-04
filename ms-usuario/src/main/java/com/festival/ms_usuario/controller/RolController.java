package com.festival.ms_usuario.controller;

import com.festival.ms_usuario.model.Rol;
import com.festival.ms_usuario.repository.RolRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolRepository rolRepository;

    @GetMapping
    public ResponseEntity<List<Rol>> listar() {
        return ResponseEntity.ok(rolRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Rol> crear(@RequestBody Rol rol) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolRepository.save(rol));
    }
}