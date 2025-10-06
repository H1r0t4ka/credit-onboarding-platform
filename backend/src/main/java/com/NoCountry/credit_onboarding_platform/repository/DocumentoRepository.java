package com.NoCountry.credit_onboarding_platform.repository;

import com.NoCountry.credit_onboarding_platform.model.Documento;
import com.NoCountry.credit_onboarding_platform.model.Documento.TipoDocumento;
import com.NoCountry.credit_onboarding_platform.model.Documento.EstadoDocumento;
import com.NoCountry.credit_onboarding_platform.model.Solicitud;
import com.NoCountry.credit_onboarding_platform.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    
    // Buscar por solicitud
    List<Documento> findBySolicitud(Solicitud solicitud);
    
    List<Documento> findBySolicitudId(Long solicitudId);
    
    // Buscar por cliente
    List<Documento> findByCliente(Usuario cliente);
    
    List<Documento> findByClienteId(Long clienteId);
    
    // Buscar por tipo de documento
    List<Documento> findByTipoDocumento(TipoDocumento tipoDocumento);
    
    // Buscar por estado
    List<Documento> findByEstado(EstadoDocumento estado);
    
    // Documentos KYC de un cliente
    @Query("SELECT d FROM Documento d WHERE d.cliente.id = :clienteId AND d.tipoDocumento LIKE 'KYC%'")
    List<Documento> findDocumentosKYCByCliente(@Param("clienteId") Long clienteId);
    
    // Documentos de préstamo por solicitud
    @Query("SELECT d FROM Documento d WHERE d.solicitud.id = :solicitudId AND d.tipoDocumento LIKE 'PRESTAMO%'")
    List<Documento> findDocumentosPrestamosBySolicitud(@Param("solicitudId") Long solicitudId);
    
    // Documentos pendientes de validación
    List<Documento> findByEstadoOrderByFechaCargaDesc(EstadoDocumento estado);
    
    // Contar documentos por solicitud y estado
    Long countBySolicitudIdAndEstado(Long solicitudId, EstadoDocumento estado);
    
    // Verificar si existe un tipo de documento para una solicitud
    boolean existsBySolicitudIdAndTipoDocumento(Long solicitudId, TipoDocumento tipoDocumento);
}