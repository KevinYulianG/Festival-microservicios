package com.festival.ms_lineup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "programacion_artista")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgramacionArtista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "artista_id", nullable = false)
    private Artista artista;

    @Column(name = "evento_id", nullable = false)
    private Long eventoId;

    @Column(name = "nombre_escenario", nullable = false)
    private String nombreEscenario;

    @Column(name = "hora_inicio", nullable = false)
    private LocalDateTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalDateTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProgramacion estado;
}