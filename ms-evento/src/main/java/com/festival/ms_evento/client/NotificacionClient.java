package com.festival.ms_evento.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-notificacion", url = "${ms.notificaciones.url}", path = "/api/notificaciones")
public interface NotificacionClient {

    @PostMapping
    void enviarNotificacion(@RequestBody NotificacionRequestDto request);
}
