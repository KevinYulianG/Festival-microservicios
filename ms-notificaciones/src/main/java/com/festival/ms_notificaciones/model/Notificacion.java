package com.festival.ms_notificaciones.model;

import java.time.LocalDateTime;

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
@Table(name = "notificacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notificacion {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo;
    @Column(nullable = false)
    private String mensaje;
    @Column(nullable = false)
    private Long usuarioId;
    
    private LocalDateTime fechaEnvio;

}
