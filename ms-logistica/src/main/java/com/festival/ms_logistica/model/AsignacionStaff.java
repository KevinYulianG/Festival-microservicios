package com.festival.ms_logistica.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asignacion_staff")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionStaff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String usuarioNombre;
    
    @Column(nullable = false)
    private String labor;

    @ManyToOne
    @JoinColumn(name = "zona_id")
    private Zona zona;



}
