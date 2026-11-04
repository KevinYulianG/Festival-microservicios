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

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "evento_id", nullable = false)
    private Long eventoId;

    @Column(name = "codigo_qr", unique = true, nullable = false)
    private String codigoQr;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketEstado estado;

    @Column(name = "tipo_entrada", nullable = false)
    private String tipoEntrada;

    @Column(name = "precio_pagado", nullable = false)
    private BigDecimal precioPagado;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;

    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;
}