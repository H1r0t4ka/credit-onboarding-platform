package com.NoCountry.credit_onboarding_platform.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialEstado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;
    
    @Column(name = "estado_anterior", length = 50)
    private String estadoAnterior;
    
    @Column(name = "estado_nuevo", nullable = false, length = 50)
    private String estadoNuevo;
    
    @Column(length = 1000)
    private String comentario;
    
    @ManyToOne
    @JoinColumn(name = "modificado_por")
    private Usuario modificadoPor;
    
    @Column(name = "fecha_cambio")
    @Builder.Default
    private LocalDateTime fechaCambio = LocalDateTime.now();
}