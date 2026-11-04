package com.festival.ms_logistica.util;

import com.festival.ms_logistica.dto.EventoDTO;
import com.festival.ms_logistica.dto.ZonaDTO;
import com.festival.ms_logistica.model.Zona;

public class ZonaTestDataFactory {

    public static ZonaDTO zonaDtoFalsa() {
        ZonaDTO dto = new ZonaDTO();
        dto.setNombre("Zona Primeros Auxilios");
        dto.setTipo("SALUD");
        dto.setEventoId(1L);
        dto.setEventoNombre("Festival Reggae 2025");
        return dto;
    }

    public static Zona zonaEntidadFalsa() {
        Zona zona = new Zona();
        zona.setId(1L);
        zona.setNombre("Zona Primeros Auxilios");
        zona.setTipo("SALUD");
        zona.setEventoId(1L);
        zona.setEventoNombre("Festival Reggae 2025");
        return zona;
    }

    public static EventoDTO eventoPublicadoFalso() {
        EventoDTO evento = new EventoDTO();
        evento.setNombre("Festival Reggae 2025");
        evento.setEstado("PUBLICADO");
        return evento;
    }

    public static EventoDTO eventoNoPublicadoFalso() {
        EventoDTO evento = new EventoDTO();
        evento.setNombre("Festival Reggae 2025");
        evento.setEstado("BORRADOR");
        return evento;
    }
}