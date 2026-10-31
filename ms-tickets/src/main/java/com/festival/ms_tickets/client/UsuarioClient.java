package com.festival.ms_tickets.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.festival.ms_tickets.dto.UsuarioRespuestaDTO;

@FeignClient(name = "ms-usuarios", url = "${ms.usuarios.url}")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioRespuestaDTO obtenerUsuario(@PathVariable Long id);
}