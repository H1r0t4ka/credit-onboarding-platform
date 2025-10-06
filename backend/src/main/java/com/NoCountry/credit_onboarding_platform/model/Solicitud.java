package com.NoCountry.credit_onboarding_platform.model;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montoSolicitado;
    
    @Column(nullable = false)
    private Integer plazoMeses;
    
    @Column(length = 500)
    private String propositoCredito;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;
    
    @Column(name = "fecha_solicitud")
    @Builder.Default
    private LocalDateTime fechaSolicitud = LocalDateTime.now();
    
    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;
    
    @Column(name = "comentario_evaluacion", length = 1000)
    private String comentarioEvaluacion;
    
    // Campos adicionales para el flujo de trabajo
    @Column(name = "paso_actual")
    @Builder.Default
    private Integer pasoActual = 1;
    
    @Column(name = "datos_formulario", columnDefinition = "TEXT")
    private String datosFormulario;
    
    @Column(name = "fecha_actualizacion")
    @Builder.Default
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
    
    @Column(name = "comentario_revision", length = 1000)
    private String comentarioRevision;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;
    
    @ManyToOne
    @JoinColumn(name = "revisado_por_id")
    private Usuario revisadoPor;
    
    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Documento> documentos;
    
    // Enums
    public enum EstadoSolicitud {
        BORRADOR, PENDIENTE, EN_REVISION, APROBADA, RECHAZADA, CANCELADA
    }
}