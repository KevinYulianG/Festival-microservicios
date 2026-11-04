package com.festival.ms_lineup.client;

import com.festival.ms_lineup.dto.EventoDTORespuesta;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-eventos", url = "${ms.eventos.url}")
public interface EventoClient {

    @GetMapping("/api/eventos/{id}")
    EventoDTORespuesta obtenerEvento(@PathVariable Long id);
}