package com.festival.ms_evento.exception;

public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
