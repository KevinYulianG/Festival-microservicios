package com.festival.ms_tickets.exception;

public class EventoNoDisponibleException extends RuntimeException {
    public EventoNoDisponibleException(String message) {
        super(message);
    }
}