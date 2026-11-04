package com.festival.ms_usuario.client;

import com.festival.ms_usuario.dto.EventoDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventoClient {

    private static final Logger log = LoggerFactory.getLogger(EventoClient.class);

    private final RestTemplate restTemplate;

    @Value("${ms.eventos.url}")
    private String eventosUrl;

    private String getBaseUrl() {
        return eventosUrl + "/api/eventos";
    }

    public List<EventoDto> obtenerEventosPublicados() {
        try {
            ResponseEntity<List<EventoDto>> response = restTemplate.exchange(
                getBaseUrl() + "/publicados",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<EventoDto>>() {}
            );
            List<EventoDto> eventos = response.getBody();
            log.info("Eventos publicados obtenidos desde ms-evento: {}",
                eventos != null ? eventos.size() : 0);
            return eventos != null ? eventos : Collections.emptyList();
        } catch (Exception e) {
            log.warn("No se pudo obtener eventos publicados desde ms-evento: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean isEventoPublicado(Long eventoId) {
        try {
            Boolean publicado = restTemplate.getForObject(
                getBaseUrl() + "/" + eventoId + "/publicado",
                Boolean.class
            );
            log.info("Verificacion evento {}: publicado={}", eventoId, publicado);
            return Boolean.TRUE.equals(publicado);
        } catch (Exception e) {
            log.warn("No se pudo verificar estado del evento {}: {}", eventoId, e.getMessage());
            return false;
        }
    }

    public EventoDto obtenerEventoPorId(Long eventoId) {
        try {
            EventoDto evento = restTemplate.getForObject(
                getBaseUrl() + "/" + eventoId,
                EventoDto.class
            );
            log.info("Evento {} obtenido desde ms-evento: {}", eventoId,
                evento != null ? evento.getNombre() : "null");
            return evento;
        } catch (Exception e) {
            log.warn("No se pudo obtener evento {}: {}", eventoId, e.getMessage());
            return null;
        }
    }
}