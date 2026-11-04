package com.festival.ms_logistica.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "escenario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Escenario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private Integer capacidad;
    @Column(nullable = false)
    private Long eventoId;
    @Column(nullable = false)
    private String eventoNombre;
}
