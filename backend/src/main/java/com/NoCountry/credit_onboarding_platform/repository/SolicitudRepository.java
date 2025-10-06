package com.NoCountry.credit_onboarding_platform.repository;

import com.NoCountry.credit_onboarding_platform.model.Solicitud;
import com.NoCountry.credit_onboarding_platform.model.Solicitud.EstadoSolicitud;
import com.NoCountry.credit_onboarding_platform.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    
    // Buscar por cliente
    List<Solicitud> findByCliente(Usuario cliente);
    
    List<Solicitud> findByClienteId(Long clienteId);
    
    // Buscar por estado
    List<Solicitud> findByEstado(EstadoSolicitud estado);
    
    // Buscar por estado ordenadas por fecha
    List<Solicitud> findByEstadoOrderByFechaSolicitudDesc(EstadoSolicitud estado);
    
    // Solicitudes en revisión
    @Query("SELECT s FROM Solicitud s WHERE s.estado = 'EN_REVISION' ORDER BY s.fechaSolicitud ASC")
    List<Solicitud> findSolicitudesEnRevision();
    
    // Solicitudes por revisor
    List<Solicitud> findByRevisadoPor(Usuario admin);
    
    List<Solicitud> findByRevisadoPorId(Long adminId);
    
    // Solicitudes por rango de fecha
    List<Solicitud> findByFechaSolicitudBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Solicitudes por cliente y estado
    List<Solicitud> findByClienteIdAndEstado(Long clienteId, EstadoSolicitud estado);
    
    // Buscar por monto mínimo
    List<Solicitud> findByMontoSolicitadoGreaterThanEqual(BigDecimal monto);
    
    // Contar solicitudes por estado
    Long countByEstado(EstadoSolicitud estado);
    
    // Solicitudes recientes (últimos 30 días)
    @Query("SELECT s FROM Solicitud s WHERE s.fechaSolicitud >= :fechaInicio ORDER BY s.fechaSolicitud DESC")
    List<Solicitud> findSolicitudesRecientes(@Param("fechaInicio") LocalDateTime fechaInicio);
    
    // Dashboard: solicitudes por cliente con estado
    @Query("SELECT s FROM Solicitud s WHERE s.cliente.id = :clienteId ORDER BY s.fechaActualizacion DESC")
    List<Solicitud> findByClienteIdOrderByFechaActualizacionDesc(@Param("clienteId") Long clienteId);
    
    // Solicitudes sin asignar (sin revisor)
    @Query("SELECT s FROM Solicitud s WHERE s.revisadoPor IS NULL AND s.estado = 'EN_REVISION'")
    List<Solicitud> findSolicitudesSinAsignar();
}
