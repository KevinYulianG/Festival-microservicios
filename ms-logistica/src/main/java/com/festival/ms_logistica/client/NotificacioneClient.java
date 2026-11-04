package com.festival.ms_logistica.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.festival.ms_logistica.dto.NotificacionDTO;
import com.festival.ms_logistica.security.FeignClientConfig; // <-- Importamos tu interceptor seguro

@FeignClient(name = "ms-notificaciones", url = "${ms.notificaciones.url}", configuration = FeignClientConfig.class)
public interface NotificacioneClient {
    
    @PostMapping("/api/notificaciones")
    NotificacionDTO crearNotificacion(@RequestBody NotificacionDTO dto);
}