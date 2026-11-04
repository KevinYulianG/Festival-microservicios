package com.festival.ms_tickets.client;

import com.festival.ms_tickets.dto.EventoRespuestaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-eventos", url = "${ms.eventos.url}")
public interface EventoClient {

    @GetMapping("/api/eventos/{id}")
    EventoRespuestaDTO obtenerEvento(@PathVariable Long id);
}