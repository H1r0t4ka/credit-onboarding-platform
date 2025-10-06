package com.NoCountry.credit_onboarding_platform.repository;

import com.NoCountry.credit_onboarding_platform.model.FirmaDigital;
import com.NoCountry.credit_onboarding_platform.model.Solicitud;
import com.NoCountry.credit_onboarding_platform.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FirmaDigitalRepository extends JpaRepository<FirmaDigital, Long> {
    
    // Buscar firma por solicitud
    Optional<FirmaDigital> findBySolicitud(Solicitud solicitud);
    
    Optional<FirmaDigital> findBySolicitudId(Long solicitudId);
    
    // Buscar firma por documento
    Optional<FirmaDigital> findByDocumento(Documento documento);
    
    Optional<FirmaDigital> findByDocumentoId(Long documentoId);
    
    // Buscar por email del firmante
    List<FirmaDigital> findByFirmanteEmail(String email);
    
    // Buscar por rango de fechas
    List<FirmaDigital> findByFechaFirmaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Verificar si una solicitud ya tiene firma
    boolean existsBySolicitudId(Long solicitudId);
    
    // Firmas recientes
    @Query("SELECT f FROM FirmaDigital f ORDER BY f.fechaFirma DESC")
    List<FirmaDigital> findAllOrderByFechaFirmaDesc();
    
    // Contar firmas por día
    @Query("SELECT COUNT(f) FROM FirmaDigital f WHERE DATE(f.fechaFirma) = DATE(:fecha)")
    Long countFirmasPorDia(@Param("fecha") LocalDateTime fecha);
}