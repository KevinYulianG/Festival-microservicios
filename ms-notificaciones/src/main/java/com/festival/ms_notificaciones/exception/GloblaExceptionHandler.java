package com.festival.ms_notificaciones.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GloblaExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> manejarNotificacionNoEncontrada(ResourceNotFoundException ex)
    {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "NOT FOUND",
            ex.getMessage()

        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorGeneral(Exception ex)
    {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "ERROR SERVIDOR",
            "Ocurrio un error inesperado"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
