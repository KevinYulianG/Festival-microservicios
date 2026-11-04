package com.festival.ms_tickets.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExcepcionGlobal {

    @ExceptionHandler(TicketNoEncontradoException.class)
    public ResponseEntity<String> handleNotFound(TicketNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(AforoAgotadoException.class)
    public ResponseEntity<String> handleAforo(AforoAgotadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(TicketDuplicadoException.class)
    public ResponseEntity<String> handleDuplicado(TicketDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(TicketYaUsadoException.class)
    public ResponseEntity<String> handleYaUsado(TicketYaUsadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(EventoNoDisponibleException.class)
    public ResponseEntity<String> handleEventoNoDisponible(EventoNoDisponibleException ex) {
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