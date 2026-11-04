package com.festival.ms_evento.model;

// Enum que define los estados posibles de un evento
// La maquina de estados controla que operaciones estan permitidas
public enum EstadoEvento {
    BORRADOR,    // Recien creado, no visible para asistentes
    PUBLICADO,   // Activo: se pueden vender tickets y programar artistas
    CANCELADO,   // Cancelado: no se pueden hacer operaciones
    FINALIZADO   // Terminado: el festival ya ocurrio
}