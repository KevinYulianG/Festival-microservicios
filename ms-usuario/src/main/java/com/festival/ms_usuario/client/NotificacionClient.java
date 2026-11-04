package com.festival.ms_usuario.client;

import com.festival.ms_usuario.dto.NotificacionDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NotificacionClient {

    private static final Logger log = LoggerFactory.getLogger(NotificacionClient.class);
    private final RestTemplate restTemplate;

    @Value("${ms.notificaciones.url}")
    private String notificacionesUrl;

    public void enviarNotificacion(NotificacionDto notificacion) {
        try {
            restTemplate.postForObject(
                notificacionesUrl + "/api/notificaciones",
                notificacion,
                Void.class
            );
            log.info("Notificacion enviada: tipo={}, usuarioId={}",
                notificacion.getTipo(), notificacion.getUsuarioId());
        } catch (Exception e) {
            log.warn("No se pudo enviar notificacion: {}", e.getMessage());
        }
    }
}