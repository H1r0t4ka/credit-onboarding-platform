package com.NoCountry.credit_onboarding_platform.controller;

import com.NoCountry.credit_onboarding_platform.model.FirmaDigital;
import com.NoCountry.credit_onboarding_platform.service.FirmaDigitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/firmas")
@CrossOrigin(origins = "*")
public class FirmaDigitalController {
    
    @Autowired
    private FirmaDigitalService firmaService;
    
    // ========== CRUD Básico ==========
    
    @GetMapping
    public ResponseEntity<List<FirmaDigital>> getAllFirmas() {
        List<FirmaDigital> firmas = firmaService.findAll();
        return ResponseEntity.ok(firmas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FirmaDigital> getFirmaById(@PathVariable Long id) {
        Optional<FirmaDigital> firma = firmaService.findById(id);
        return firma.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFirma(@PathVariable Long id) {
        if (!firmaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        firmaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // ========== Endpoints de Negocio ==========
    
    @PostMapping("/firmar")
    public ResponseEntity<?> firmarSolicitud(@RequestBody Map<String, String> body) {
        try {
            Long solicitudId = Long.parseLong(body.get("solicitudId"));
            String firmante = body.get("firmante");
            String email = body.get("email");
            String ipAddress = body.get("ipAddress");
            
            FirmaDigital firma = firmaService.firmarSolicitud(solicitudId, firmante, email, ipAddress);
            return ResponseEntity.status(HttpStatus.CREATED).body(firma);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/solicitud/{solicitudId}")
    public ResponseEntity<?> getFirmaPorSolicitud(@PathVariable Long solicitudId) {
        Optional<FirmaDigital> firma = firmaService.obtenerFirmaPorSolicitud(solicitudId);
        return firma.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/solicitud/{solicitudId}/esta-firmada")
    public ResponseEntity<Map<String, Boolean>> verificarSolicitudFirmada(@PathVariable Long solicitudId) {
        boolean firmada = firmaService.solicitudEstafirmada(solicitudId);
        return ResponseEntity.ok(Map.of("firmada", firmada));
    }
    
    @PostMapping("/{id}/verificar")
    public ResponseEntity<Map<String, Boolean>> verificarFirma(
        @PathVariable Long id,
        @RequestBody Map<String, String> body
    ) {
        try {
            String hashProvisto = body.get("hash");
            boolean valida = firmaService.verificarFirma(id, hashProvisto);
            return ResponseEntity.ok(Map.of("valida", valida));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("valida", false));
        }
    }
    
    @GetMapping("/recientes")
    public ResponseEntity<List<FirmaDigital>> getFirmasRecientes() {
        List<FirmaDigital> firmas = firmaService.getFirmasRecientes();
        return ResponseEntity.ok(firmas);
    }
    
    @GetMapping("/contar-por-dia")
    public ResponseEntity<Map<String, Long>> contarFirmasPorDia(@RequestParam String fecha) {
        try {
            LocalDateTime fechaParam = LocalDateTime.parse(fecha);
            Long cantidad = firmaService.contarFirmasPorDia(fechaParam);
            return ResponseEntity.ok(Map.of("cantidad", cantidad));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("cantidad", 0L));
        }
    }
}