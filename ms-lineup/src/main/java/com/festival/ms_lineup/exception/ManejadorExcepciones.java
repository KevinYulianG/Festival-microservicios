package com.festival.ms_lineup.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ManejadorExcepciones {

    @ExceptionHandler(ArtistaNoEncontrado.class)
    public ResponseEntity<String> handleArtista(ArtistaNoEncontrado ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ProgramacionNoEncontrada.class)
    public ResponseEntity<String> handleProgramacion(ProgramacionNoEncontrada ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ConflictoHorario.class)
    public ResponseEntity<String> handleConflicto(ConflictoHorario ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(EventoNoDisponible.class)
    public ResponseEntity<String> handleEvento(EventoNoDisponible ex) {
        return ResponseEntity.status(422).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errores);
    }
}