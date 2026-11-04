package com.festival.ms_notificaciones.exception;

public class ResourceNotFoundException  extends RuntimeException{
    
    public ResourceNotFoundException(String mensaje){
        super(mensaje);
    }
}
