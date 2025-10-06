package com.NoCountry.credit_onboarding_platform.controller;

import com.NoCountry.credit_onboarding_platform.model.Solicitud;
import com.NoCountry.credit_onboarding_platform.model.Solicitud.EstadoSolicitud;
import com.NoCountry.credit_onboarding_platform.service.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudController {
    
    @Autowired
    private SolicitudService solicitudService;
    
    // ========== CRUD Básico ==========
    
    @GetMapping
    public ResponseEntity<List<Solicitud>> getAllSolicitudes() {
        List<Solicitud> solicitudes = solicitudService.findAll();
        return ResponseEntity.ok(solicitudes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> getSolicitudById(@PathVariable Long id) {
        Optional<Solicitud> solicitud = solicitudService.findById(id);
        return solicitud.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Solicitud> createSolicitud(@RequestBody Solicitud solicitud) {
        Solicitud nueva = solicitudService.save(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Solicitud> updateSolicitud(@PathVariable Long id, @RequestBody Solicitud solicitud) {
        if (!solicitudService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        solicitud.setId(id);
        Solicitud actualizada = solicitudService.save(solicitud);
        return ResponseEntity.ok(actualizada);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolicitud(@PathVariable Long id) {
        if (!solicitudService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        solicitudService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // ========== Endpoints de Negocio ==========
    
    @PostMapping("/crear")
    public ResponseEntity<?> crearSolicitud(@RequestBody Solicitud solicitud, @RequestParam Long clienteId) {
        try {
            Solicitud nueva = solicitudService.crearSolicitud(solicitud, clienteId);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/guardar-avance")
    public ResponseEntity<?> guardarAvance(
        @PathVariable Long id,
        @RequestBody Map<String, Object> body
    ) {
        try {
            String datosFormulario = (String) body.get("datosFormulario");
            Integer pasoActual = (Integer) body.get("pasoActual");
            
            Solicitud actualizada = solicitudService.guardarAvance(id, datosFormulario, pasoActual);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/enviar-revision")
    public ResponseEntity<?> enviarARevision(@PathVariable Long id) {
        try {
            Solicitud actualizada = solicitudService.enviarARevision(id);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/asignar-revisor")
    public ResponseEntity<?> asignarRevisor(@PathVariable Long id, @RequestParam Long adminId) {
        try {
            Solicitud actualizada = solicitudService.asignarRevisor(id, adminId);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/cambiar-estado")
    public ResponseEntity<?> cambiarEstado(
        @PathVariable Long id,
        @RequestBody Map<String, String> body,
        @RequestParam Long adminId
    ) {
        try {
            EstadoSolicitud nuevoEstado = EstadoSolicitud.valueOf(body.get("estado"));
            String comentario = body.get("comentario");
            
            Solicitud actualizada = solicitudService.cambiarEstado(id, nuevoEstado, comentario, adminId);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Solicitud>> getSolicitudesPorCliente(@PathVariable Long clienteId) {
        List<Solicitud> solicitudes = solicitudService.getSolicitudesPorCliente(clienteId);
        return ResponseEntity.ok(solicitudes);
    }
    
    @GetMapping("/en-revision")
    public ResponseEntity<List<Solicitud>> getSolicitudesEnRevision() {
        List<Solicitud> solicitudes = solicitudService.getSolicitudesEnRevision();
        return ResponseEntity.ok(solicitudes);
    }
    
    @GetMapping("/sin-asignar")
    public ResponseEntity<List<Solicitud>> getSolicitudesSinAsignar() {
        List<Solicitud> solicitudes = solicitudService.getSolicitudesSinAsignar();
        return ResponseEntity.ok(solicitudes);
    }
    
    @GetMapping("/revisor/{adminId}")
    public ResponseEntity<List<Solicitud>> getSolicitudesPorRevisor(@PathVariable Long adminId) {
        List<Solicitud> solicitudes = solicitudService.getSolicitudesPorRevisor(adminId);
        return ResponseEntity.ok(solicitudes);
    }
    
    @GetMapping("/contar/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstado(@PathVariable String estado) {
        EstadoSolicitud estadoEnum = EstadoSolicitud.valueOf(estado);
        Long cantidad = solicitudService.contarPorEstado(estadoEnum);
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }
    
    @GetMapping("/recientes")
    public ResponseEntity<List<Solicitud>> getSolicitudesRecientes(@RequestParam(defaultValue = "30") int dias) {
        List<Solicitud> solicitudes = solicitudService.getSolicitudesRecientes(dias);
        return ResponseEntity.ok(solicitudes);
    }
}