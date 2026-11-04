package com.festival.ms_logistica.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.festival.ms_logistica.dto.EventoDTO;


@FeignClient(name = "ms-eventos", url = "${ms.eventos.url}")
public interface EventoClient {
    
    @GetMapping("/api/eventos/{id}")
    EventoDTO obtenerEvento(@PathVariable Long id);
}