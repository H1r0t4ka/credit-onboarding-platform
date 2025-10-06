package com.NoCountry.credit_onboarding_platform.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 20)
    private String telefono;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;
    
    // Campos para CLIENTES (nullables para ADMIN)
    @Column(length = 50)
    private String rfc;
    
    @Column(length = 200)
    private String razonSocial;
    
    @Column(length = 300)
    private String direccion;
    
    @Column(length = 100)
    private String representanteLegal;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;
    
    @Column(name = "fecha_creacion", updatable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    // Enums
    public enum Rol {
        ADMIN, CLIENTE
    }
    
    public enum EstadoUsuario {
        ACTIVO, INACTIVO, BLOQUEADO
    }
}