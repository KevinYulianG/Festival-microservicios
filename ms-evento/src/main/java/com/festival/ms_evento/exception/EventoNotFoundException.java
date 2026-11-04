package com.festival.ms_evento.exception;

public class EventoNotFoundException extends RuntimeException {
    public EventoNotFoundException(Long id) {
        super("Evento no encontrado con ID: " + id);
    }
}
