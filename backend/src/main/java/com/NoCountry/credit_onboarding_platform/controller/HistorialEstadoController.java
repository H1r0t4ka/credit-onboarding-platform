package com.NoCountry.credit_onboarding_platform.controller;

import com.NoCountry.credit_onboarding_platform.model.HistorialEstado;
import com.NoCountry.credit_onboarding_platform.service.HistorialEstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/historial")
@CrossOrigin(origins = "*")
public class HistorialEstadoController {
    
    @Autowired
    private HistorialEstadoService historialService;
    
    // ========== CRUD Básico ==========
    
    @GetMapping
    public ResponseEntity<List<HistorialEstado>> getAllHistorial() {
        List<HistorialEstado> historial = historialService.findAll();
        return ResponseEntity.ok(historial);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<HistorialEstado> getHistorialById(@PathVariable Long id) {
        Optional<HistorialEstado> historial = historialService.findById(id);
        return historial.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistorial(@PathVariable Long id) {
        if (!historialService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        historialService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // ========== Endpoints de Negocio ==========
    
    @GetMapping("/solicitud/{solicitudId}")
    public ResponseEntity<List<HistorialEstado>> getHistorialPorSolicitud(@PathVariable Long solicitudId) {
        List<HistorialEstado> historial = historialService.getHistorialPorSolicitud(solicitudId);
        return ResponseEntity.ok(historial);
    }
    
    @GetMapping("/solicitud/{solicitudId}/ultimos")
    public ResponseEntity<List<HistorialEstado>> getUltimosCambios(@PathVariable Long solicitudId) {
        List<HistorialEstado> historial = historialService.getUltimosCambios(solicitudId);
        return ResponseEntity.ok(historial);
    }
    
    @GetMapping("/actividad-reciente")
    public ResponseEntity<List<HistorialEstado>> getActividadReciente() {
        List<HistorialEstado> historial = historialService.getActividadReciente();
        return ResponseEntity.ok(historial);
    }
    
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<HistorialEstado>> getCambiosPorAdmin(@PathVariable Long adminId) {
        List<HistorialEstado> historial = historialService.getCambiosPorAdmin(adminId);
        return ResponseEntity.ok(historial);
    }
    
    @GetMapping("/periodo")
    public ResponseEntity<List<HistorialEstado>> getCambiosPorPeriodo(
        @RequestParam String inicio,
        @RequestParam String fin
    ) {
        try {
            LocalDateTime fechaInicio = LocalDateTime.parse(inicio);
            LocalDateTime fechaFin = LocalDateTime.parse(fin);
            
            List<HistorialEstado> historial = historialService.getCambiosPorPeriodo(fechaInicio, fechaFin);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/solicitud/{solicitudId}/contar")
    public ResponseEntity<Map<String, Long>> contarCambiosPorSolicitud(@PathVariable Long solicitudId) {
        Long cantidad = historialService.contarCambiosPorSolicitud(solicitudId);
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<HistorialEstado>> getCambiosAEstado(@PathVariable String estado) {
        List<HistorialEstado> historial = historialService.getCambiosAEstado(estado);
        return ResponseEntity.ok(historial);
    }
}