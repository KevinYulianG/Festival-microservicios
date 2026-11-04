package com.festival.ms_tickets.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.festival.ms_tickets.dto.NotificacionPedidoDTO;

@FeignClient(name = "ms-notificaciones", url = "${ms.notificaciones.url}" )
public interface NotificacionClient {

    @PostMapping("/api/notificaciones")
    void enviarNotificacion(@RequestBody NotificacionPedidoDTO dto);
}