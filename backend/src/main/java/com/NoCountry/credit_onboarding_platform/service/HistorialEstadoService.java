package com.NoCountry.credit_onboarding_platform.service;

import com.NoCountry.credit_onboarding_platform.model.HistorialEstado;
import com.NoCountry.credit_onboarding_platform.model.Solicitud;
import com.NoCountry.credit_onboarding_platform.model.Usuario;
import com.NoCountry.credit_onboarding_platform.repository.HistorialEstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HistorialEstadoService {
    
    @Autowired
    private HistorialEstadoRepository historialRepository;
    
    // ========== CRUD Básico ==========
    
    public List<HistorialEstado> findAll() {
        return historialRepository.findAll();
    }
    
    public Optional<HistorialEstado> findById(Long id) {
        return historialRepository.findById(id);
    }
    
    public HistorialEstado save(HistorialEstado historial) {
        return historialRepository.save(historial);
    }
    
    public void deleteById(Long id) {
        historialRepository.deleteById(id);
    }
    
    // ========== Métodos de Negocio ==========
    
    public HistorialEstado registrarCambioEstado(
        Solicitud solicitud, 
        String estadoAnterior, 
        String estadoNuevo, 
        String comentario, 
        Usuario modificadoPor
    ) {
        HistorialEstado historial = HistorialEstado.builder()
            .solicitud(solicitud)
            .estadoAnterior(estadoAnterior)
            .estadoNuevo(estadoNuevo)
            .comentario(comentario)
            .modificadoPor(modificadoPor)
            .fechaCambio(LocalDateTime.now())
            .build();
        
        return historialRepository.save(historial);
    }
    
    public List<HistorialEstado> getHistorialPorSolicitud(Long solicitudId) {
        return historialRepository.findBySolicitudIdOrderByFechaCambioDesc(solicitudId);
    }
    
    public List<HistorialEstado> getUltimosCambios(Long solicitudId) {
        return historialRepository.findUltimosCambiosBySolicitud(solicitudId);
    }
    
    public List<HistorialEstado> getActividadReciente() {
        return historialRepository.findActividadReciente();
    }
    
    public List<HistorialEstado> getCambiosPorAdmin(Long adminId) {
        return historialRepository.findByModificadoPorId(adminId);
    }
    
    public List<HistorialEstado> getCambiosPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return historialRepository.findByFechaCambioBetween(inicio, fin);
    }
    
    public Long contarCambiosPorSolicitud(Long solicitudId) {
        return historialRepository.countBySolicitudId(solicitudId);
    }
    
    public List<HistorialEstado> getCambiosAEstado(String estadoNuevo) {
        return historialRepository.findByEstadoNuevo(estadoNuevo);
    }
}