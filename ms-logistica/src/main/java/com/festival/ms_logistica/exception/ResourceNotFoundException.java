package com.festival.ms_logistica.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String mensaje){
        super(mensaje);
    }
}
