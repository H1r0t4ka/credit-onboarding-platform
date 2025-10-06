package com.NoCountry.credit_onboarding_platform.service;

import com.NoCountry.credit_onboarding_platform.model.FirmaDigital;
import com.NoCountry.credit_onboarding_platform.model.Solicitud;
import com.NoCountry.credit_onboarding_platform.repository.FirmaDigitalRepository;
import com.NoCountry.credit_onboarding_platform.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FirmaDigitalService {
    
    @Autowired
    private FirmaDigitalRepository firmaRepository;
    
    @Autowired
    private SolicitudRepository solicitudRepository;
    
    // ========== CRUD Básico ==========
    
    public List<FirmaDigital> findAll() {
        return firmaRepository.findAll();
    }
    
    public Optional<FirmaDigital> findById(Long id) {
        return firmaRepository.findById(id);
    }
    
    public FirmaDigital save(FirmaDigital firma) {
        return firmaRepository.save(firma);
    }
    
    public void deleteById(Long id) {
        firmaRepository.deleteById(id);
    }
    
    // ========== Métodos de Negocio ==========
    
    public FirmaDigital firmarSolicitud(Long solicitudId, String firmante, String email, String ipAddress) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        // Verificar si ya está firmada
        if (firmaRepository.existsBySolicitudId(solicitudId)) {
            throw new RuntimeException("La solicitud ya ha sido firmada");
        }
        
        // Generar hash de la firma
        String hash = generarHashFirma(solicitudId, firmante, LocalDateTime.now().toString());
        
        FirmaDigital firma = FirmaDigital.builder()
            .solicitud(solicitud)
            .hashFirma(hash)
            .firmante(firmante)
            .firmanteEmail(email)
            .ipAddress(ipAddress)
            .fechaFirma(LocalDateTime.now())
            .build();
        
        return firmaRepository.save(firma);
    }
    
    public Optional<FirmaDigital> obtenerFirmaPorSolicitud(Long solicitudId) {
        return firmaRepository.findBySolicitudId(solicitudId);
    }
    
    public boolean solicitudEstafirmada(Long solicitudId) {
        return firmaRepository.existsBySolicitudId(solicitudId);
    }
    
    public boolean verificarFirma(Long firmaId, String hashProvisto) {
        FirmaDigital firma = firmaRepository.findById(firmaId)
            .orElseThrow(() -> new RuntimeException("Firma no encontrada"));
        
        return firma.getHashFirma().equals(hashProvisto);
    }
    
    public List<FirmaDigital> getFirmasRecientes() {
        return firmaRepository.findAllOrderByFechaFirmaDesc();
    }
    
    public Long contarFirmasPorDia(LocalDateTime fecha) {
        return firmaRepository.countFirmasPorDia(fecha);
    }
    
    // ========== Métodos Auxiliares ==========
    
    private String generarHashFirma(Long solicitudId, String firmante, String timestamp) {
        try {
            String datos = solicitudId + "|" + firmante + "|" + timestamp;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(datos.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash de firma", e);
        }
    }
}