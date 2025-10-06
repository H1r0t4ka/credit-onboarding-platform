package com.NoCountry.credit_onboarding_platform.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "firmas_digitales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmaDigital {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;
    
    @ManyToOne
    @JoinColumn(name = "documento_id")
    private Documento documento;
    
    @Column(name = "hash_firma", nullable = false, length = 500)
    private String hashFirma;
    
    @Column(name = "firmante_nombre", nullable = false, length = 100)
    private String firmante;
    
    @Column(name = "firmante_email", length = 100)
    private String firmanteEmail;
    
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    @Column(name = "fecha_firma")
    @Builder.Default
    private LocalDateTime fechaFirma = LocalDateTime.now();
    
    @Column(name = "certificado", columnDefinition = "TEXT")
    private String certificado; // Para almacenar certificado digital si aplica
}