package com.NoCountry.credit_onboarding_platform.service;

import com.NoCountry.credit_onboarding_platform.model.Solicitud;
import com.NoCountry.credit_onboarding_platform.model.Solicitud.EstadoSolicitud;
import com.NoCountry.credit_onboarding_platform.model.Usuario;
import com.NoCountry.credit_onboarding_platform.repository.SolicitudRepository;
import com.NoCountry.credit_onboarding_platform.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SolicitudService {
    
    @Autowired
    private SolicitudRepository solicitudRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private HistorialEstadoService historialService;
    
    @Autowired
    private DocumentoService documentoService;
    
    // ========== CRUD Básico ==========
    
    public List<Solicitud> findAll() {
        return solicitudRepository.findAll();
    }
    
    public Optional<Solicitud> findById(Long id) {
        return solicitudRepository.findById(id);
    }
    
    public Solicitud save(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }
    
    public void deleteById(Long id) {
        solicitudRepository.deleteById(id);
    }
    
    // ========== Métodos de Negocio ==========
    
    public Solicitud crearSolicitud(Solicitud solicitud, Long clienteId) {
        Usuario cliente = usuarioRepository.findById(clienteId)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        solicitud.setCliente(cliente);
        solicitud.setEstado(EstadoSolicitud.BORRADOR);
        solicitud.setPasoActual(1);
        
        Solicitud nuevaSolicitud = solicitudRepository.save(solicitud);
        
        // Registrar en historial
        historialService.registrarCambioEstado(
            nuevaSolicitud, 
            null, 
            EstadoSolicitud.BORRADOR.name(), 
            "Solicitud creada", 
            cliente
        );
        
        return nuevaSolicitud;
    }
    
    public Solicitud guardarAvance(Long solicitudId, String datosFormulario, Integer pasoActual) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        solicitud.setDatosFormulario(datosFormulario);
        solicitud.setPasoActual(pasoActual);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        
        return solicitudRepository.save(solicitud);
    }
    
    public Solicitud enviarARevision(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        // Validar que tenga documentos
        if (!documentoService.solicitudTieneDocumentosCompletos(solicitudId)) {
            throw new RuntimeException("Debe cargar y validar todos los documentos");
        }
        
        String estadoAnterior = solicitud.getEstado().name();
        solicitud.setEstado(EstadoSolicitud.EN_REVISION);
        
        Solicitud actualizada = solicitudRepository.save(solicitud);
        
        historialService.registrarCambioEstado(
            actualizada, 
            estadoAnterior, 
            EstadoSolicitud.EN_REVISION.name(), 
            "Solicitud enviada a revisión", 
            solicitud.getCliente()
        );
        
        return actualizada;
    }
    
    public Solicitud asignarRevisor(Long solicitudId, Long adminId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        Usuario admin = usuarioRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));
        
        solicitud.setRevisadoPor(admin);
        
        Solicitud actualizada = solicitudRepository.save(solicitud);
        
        historialService.registrarCambioEstado(
            actualizada, 
            null, 
            solicitud.getEstado().name(), 
            "Solicitud asignada a " + admin.getNombre(), 
            admin
        );
        
        return actualizada;
    }
    
    public Solicitud cambiarEstado(Long solicitudId, EstadoSolicitud nuevoEstado, String comentario, Long adminId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        Usuario admin = usuarioRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));
        
        String estadoAnterior = solicitud.getEstado().name();
        solicitud.setEstado(nuevoEstado);
        solicitud.setComentarioRevision(comentario);
        
        Solicitud actualizada = solicitudRepository.save(solicitud);
        
        historialService.registrarCambioEstado(
            actualizada, 
            estadoAnterior, 
            nuevoEstado.name(), 
            comentario, 
            admin
        );
        
        return actualizada;
    }
    
    public List<Solicitud> getSolicitudesPorCliente(Long clienteId) {
        return solicitudRepository.findByClienteIdOrderByFechaActualizacionDesc(clienteId);
    }
    
    public List<Solicitud> getSolicitudesEnRevision() {
        return solicitudRepository.findSolicitudesEnRevision();
    }
    
    public List<Solicitud> getSolicitudesSinAsignar() {
        return solicitudRepository.findSolicitudesSinAsignar();
    }
    
    public List<Solicitud> getSolicitudesPorRevisor(Long adminId) {
        return solicitudRepository.findByRevisadoPorId(adminId);
    }
    
    public Long contarPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.countByEstado(estado);
    }
    
    public List<Solicitud> getSolicitudesRecientes(int dias) {
        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(dias);
        return solicitudRepository.findSolicitudesRecientes(fechaInicio);
    }
}