package com.NoCountry.credit_onboarding_platform.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 50)
    private TipoDocumento tipoDocumento;
    
    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;
    
    @Column(name = "ruta_archivo", nullable = false, length = 500)
    private String rutaArchivo;
    
    @Column(name = "tamano_bytes")
    private Long tamanoBytes;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoDocumento estado = EstadoDocumento.PENDIENTE;
    
    @Column(name = "fecha_carga")
    @Builder.Default
    private LocalDateTime fechaCarga = LocalDateTime.now();
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "solicitud_id")
    private Solicitud solicitud;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;
    
    @Column(name = "comentario_validacion", length = 500)
    private String comentarioValidacion;
    
    // Enums
    public enum TipoDocumento {
        // KYC
        KYC_INE,
        KYC_COMPROBANTE_DOMICILIO,
        KYC_ACTA_CONSTITUTIVA,
        KYC_PODER_LEGAL,
        
        // Préstamo
        PRESTAMO_ESTADOS_FINANCIEROS,
        PRESTAMO_BALANCE_GENERAL,
        PRESTAMO_FLUJO_CAJA,
        PRESTAMO_DECLARACION_IMPUESTOS,
        PRESTAMO_OTROS
    }
    
    public enum EstadoDocumento {
        PENDIENTE, VALIDADO, RECHAZADO
    }
}