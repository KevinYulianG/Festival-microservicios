package com.festival.ms_tickets.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tickets")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tickets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID lógico del usuario - viene de MS-Usuarios
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    // ID lógico del evento - viene de MS-Eventos
    @Column(name = "evento_id", nullable = false)
    private Long eventoId;

    // Código único generado automáticamente con UUID
    @Column(name = "codigo_qr", unique = true, nullable = false)
    private String codigoQr;

    // Estado del ciclo de vida del ticket
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketEstado estado;

    // Tipo de entrada: GENERAL, VIP, BACKSTAGE
    @Column(name = "tipo_entrada", nullable = false)
    private String tipoEntrada;

    // Precio que pagó el asistente
    @Column(name = "precio_pagado", nullable = false)
    private BigDecimal precioPagado;

    // Fecha en que se realizó la compra
    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;

    // Fecha en que se validó en puerta (puede ser null si aún no se usa)
    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;
}