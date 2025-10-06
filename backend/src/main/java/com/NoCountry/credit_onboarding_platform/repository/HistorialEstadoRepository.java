package com.NoCountry.credit_onboarding_platform.repository;

import com.NoCountry.credit_onboarding_platform.model.HistorialEstado;
import com.NoCountry.credit_onboarding_platform.model.Solicitud;
import com.NoCountry.credit_onboarding_platform.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {
    
    // Buscar historial por solicitud
    List<HistorialEstado> findBySolicitud(Solicitud solicitud);
    
    List<HistorialEstado> findBySolicitudId(Long solicitudId);
    
    // Historial ordenado por fecha (más reciente primero)
    List<HistorialEstado> findBySolicitudIdOrderByFechaCambioDesc(Long solicitudId);
    
    // Buscar por quien modificó
    List<HistorialEstado> findByModificadoPor(Usuario usuario);
    
    List<HistorialEstado> findByModificadoPorId(Long usuarioId);
    
    // Buscar cambios a un estado específico
    List<HistorialEstado> findByEstadoNuevo(String estadoNuevo);
    
    // Buscar cambios desde un estado específico
    List<HistorialEstado> findByEstadoAnterior(String estadoAnterior);
    
    // Buscar por rango de fechas
    List<HistorialEstado> findByFechaCambioBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Último cambio de estado de una solicitud
    @Query("SELECT h FROM HistorialEstado h WHERE h.solicitud.id = :solicitudId ORDER BY h.fechaCambio DESC")
    List<HistorialEstado> findUltimosCambiosBySolicitud(@Param("solicitudId") Long solicitudId);
    
    // Contar cambios de estado por solicitud
    Long countBySolicitudId(Long solicitudId);
    
    // Actividad reciente (últimos cambios)
    @Query("SELECT h FROM HistorialEstado h ORDER BY h.fechaCambio DESC")
    List<HistorialEstado> findActividadReciente();
    
    // Cambios realizados por admin en un período
    @Query("SELECT h FROM HistorialEstado h WHERE h.modificadoPor.id = :adminId AND h.fechaCambio >= :fechaInicio")
    List<HistorialEstado> findCambiosPorAdminDesde(@Param("adminId") Long adminId, @Param("fechaInicio") LocalDateTime fechaInicio);
}